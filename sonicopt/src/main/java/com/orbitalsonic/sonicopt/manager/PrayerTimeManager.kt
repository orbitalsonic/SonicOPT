package com.orbitalsonic.sonicopt.manager

import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.models.FastingItem
import com.orbitalsonic.sonicopt.models.PrayerCustomAngle
import com.orbitalsonic.sonicopt.models.PrayerItem
import com.orbitalsonic.sonicopt.models.PrayerManualCorrection
import com.orbitalsonic.sonicopt.repository.PrayerTimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.util.Calendar
import java.util.Date

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * The `PrayerTimeManager` class is responsible for managing and providing prayer times.
 * It supports fetching daily, monthly, and yearly data based on geographic location and various calculation methods.
 */
class PrayerTimeManager {


    /**
     * Fetch Today prayer times.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchTodayPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<PrayerItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = Date()
                val calendar = Calendar.getInstance()
                calendar.time = now

                val result = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    calendar.time,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current month prayer times.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchCurrentMonthPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<PrayerItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getMonthlyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current year prayer times.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchCurrentYearPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<List<PrayerItem>>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getYearlyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch prayer times for a specific date.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     *
     * - **Parameters**:
     *   - `latitude`: The geographic latitude of the location.
     *   - `longitude`: The geographic longitude of the location.
     *   - `date`: The specific date for which prayer times are to be calculated.
     *   - `highLatitudeAdjustment`: Adjusts times for locations with high latitudes.
     *   - `asrJuristicMethod`: Specifies the juristic method for calculating Asr prayer.
     *   - `prayerTimeConvention`: The calculation convention for prayer times.
     *   - `timeFormat`: Specifies the format of prayer times (12-hour, 24-hour, etc.).
     *   - `prayerManualCorrection`: Allows manual corrections to prayer times.
     *   - `prayerCustomAngle`: Specifies custom angles for Fajr and Isha if the convention is CUSTOM.
     *   - `callback`: A callback to handle the success or failure of the operation.
     */
    fun fetchExactDatePrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<PrayerItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch prayer times for the specific date
                val result = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    date,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                // Return the result on the main thread
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                // Handle any errors during the calculation
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }


    /**
     * Fetch prayer times for a range of dates.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     *
     * - **Parameters**:
     *   - `latitude`: The geographic latitude of the location.
     *   - `longitude`: The geographic longitude of the location.
     *   - `startDate`: The start date of the range.
     *   - `endDate`: The end date of the range.
     *   - `highLatitudeAdjustment`: Adjusts times for locations with high latitudes.
     *   - `asrJuristicMethod`: Specifies the juristic method for calculating Asr prayer.
     *   - `prayerTimeConvention`: The calculation convention for prayer times.
     *   - `timeFormat`: Specifies the format of prayer times (12-hour, 24-hour, etc.).
     *   - `prayerManualCorrection`: Allows manual corrections to prayer times.
     *   - `prayerCustomAngle`: Specifies custom angles for Fajr and Isha if the convention is CUSTOM.
     *   - `callback`: A callback to handle the success or failure of the operation.
     */
    fun fetchPrayerTimesForDateRange(
        latitude: Double,
        longitude: Double,
        startDate: Date,
        endDate: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<PrayerItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch prayer times for the date range
                val result = getDateRangePrayerTimes(
                    latitude,
                    longitude,
                    startDate,
                    endDate,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                // Return the result on the main thread
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                // Handle any errors during the calculation
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }



    /**
     * Fetch today's fasting times.
     *
     * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
     *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
     *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchTodayFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<FastingItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = Date()
                val calendar = Calendar.getInstance()
                calendar.time = now

                val prayerItem = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    calendar.time,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                val fastingTimes = extractFastingTimes(prayerItem)
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current month fasting times.
     *
     * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
     *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
     *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchCurrentMonthFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<FastingItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerItems = getMonthlyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                val fastingTimes = prayerItems.map { extractFastingTimes(it) }
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current year fasting times.
     *
     * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
     *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
     *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     */
    fun fetchCurrentYearFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<List<FastingItem>>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerItems = getYearlyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                val fastingTimes = prayerItems.map { monthlyPrayerItems ->
                    monthlyPrayerItems.map { extractFastingTimes(it) }
                }
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch fasting times for a specific date.
     *
     * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
     *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
     *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     *
     * - **Parameters**:
     *   - `latitude`: The geographic latitude of the location.
     *   - `longitude`: The geographic longitude of the location.
     *   - `date`: The specific date for which fasting times are to be calculated.
     *   - `highLatitudeAdjustment`: Adjusts times for locations with high latitudes.
     *   - `prayerTimeConvention`: The calculation convention for prayer times.
     *   - `timeFormat`: Specifies the format of fasting times (12-hour, 24-hour, etc.).
     *   - `prayerManualCorrection`: Allows manual corrections to prayer times.
     *   - `prayerCustomAngle`: Specifies custom angles for Fajr and Isha if the convention is CUSTOM.
     *   - `callback`: A callback to handle the success or failure of the operation.
     */
    fun fetchExactDateFastingTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<FastingItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerItem = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    date,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                val fastingTimes = extractFastingTimes(prayerItem)
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch fasting times for a range of dates.
     *
     * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
     *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
     *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
     *
     * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
     *   If the correction is outside this range, it will default to 0 (no correction).
     *
     * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
     *   The default custom angles are:
     *      - Fajr: 9.0°
     *      - Isha: 14.0°
     *
     * - **Parameters**:
     *   - `latitude`: The geographic latitude of the location.
     *   - `longitude`: The geographic longitude of the location.
     *   - `startDate`: The start date of the range.
     *   - `endDate`: The end date of the range.
     *   - `highLatitudeAdjustment`: Adjusts times for locations with high latitudes.
     *   - `prayerTimeConvention`: The calculation convention for prayer times.
     *   - `timeFormat`: Specifies the format of fasting times (12-hour, 24-hour, etc.).
     *   - `prayerManualCorrection`: Allows manual corrections to prayer times.
     *   - `prayerCustomAngle`: Specifies custom angles for Fajr and Isha if the convention is CUSTOM.
     *   - `callback`: A callback to handle the success or failure of the operation.
     */
    fun fetchFastingTimesForDateRange(
        latitude: Double,
        longitude: Double,
        startDate: Date,
        endDate: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        prayerCustomAngle: PrayerCustomAngle = PrayerCustomAngle(),
        callback: (Result<List<FastingItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerItems = getDateRangePrayerTimes(
                    latitude,
                    longitude,
                    startDate,
                    endDate,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection,
                    prayerCustomAngle
                )
                val fastingTimes = prayerItems.map { extractFastingTimes(it) }
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * ---- Prayer Time Functions ----
     * ---- From Repository ----
     */

    private fun getDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection,
        prayerCustomAngle: PrayerCustomAngle,
    ): PrayerItem {

        return PrayerTimeRepository().getDailyPrayerTimes(
            latitude,
            longitude,
            date,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat,
            prayerManualCorrection,
            prayerCustomAngle
        )
    }

    private fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection,
        prayerCustomAngle: PrayerCustomAngle,
    ): List<PrayerItem> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return PrayerTimeRepository().getMonthlyPrayerTimes(
            latitude,
            longitude,
            currentMonth,
            currentYear,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat,
            prayerManualCorrection,
            prayerCustomAngle
        )
    }

    private fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection,
        prayerCustomAngle: PrayerCustomAngle,
    ): List<List<PrayerItem>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return PrayerTimeRepository().getYearlyPrayerTimes(
            latitude,
            longitude,
            currentYear,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat,
            prayerManualCorrection,
            prayerCustomAngle
        )
    }

    private fun getDateRangePrayerTimes(
        latitude: Double,
        longitude: Double,
        startDate: Date,
        endDate: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection,
        prayerCustomAngle: PrayerCustomAngle
    ): List<PrayerItem>  {
        return PrayerTimeRepository().getDateRangePrayerTimes(
            latitude,
            longitude,
            startDate,
            endDate,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat,
            prayerManualCorrection,
            prayerCustomAngle
        )
    }

    /**
     * Extracts fasting times (Sehri and Iftar) from the daily prayer times.
     *
     * - **Sehri**: Corresponds to the Fajr prayer time.
     * - **Iftar**: Corresponds to the Maghrib prayer time.
     */
    private fun extractFastingTimes(prayerItem: PrayerItem): FastingItem {
        val fajrTime =
            prayerItem.prayerList.firstOrNull { it.prayerName == "Fajr" }?.prayerTime ?: "--:--"
        val maghribTime =
            prayerItem.prayerList.firstOrNull { it.prayerName == "Maghrib" }?.prayerTime ?: "--:--"

        return FastingItem(
            date = prayerItem.date,
            sehriTime = fajrTime,
            iftaarTime = maghribTime
        )
    }
}


