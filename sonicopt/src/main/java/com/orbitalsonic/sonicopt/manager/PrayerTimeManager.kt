package com.orbitalsonic.sonicopt.manager

import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.models.FastingItem
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
     * Fetch today prayer times.
     */
    fun fetchTodayPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        callback: (Result<PrayerItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    asrJuristicMethod,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current month prayer times.
     */
    fun fetchCurrentMonthPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
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
                    prayerManualCorrection
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current year prayer times.
     */
    fun fetchCurrentYearPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.HANAFI,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
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
                    prayerManualCorrection
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch today fasting times.
     * Fasting times are calculated based on Fajr and Maghrib prayers,
     * Sehri ends at Fajr start time, and Iftar begins at Maghrib time.
     */
    fun fetchTodayFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
        callback: (Result<FastingItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerItem = getDailyPrayerTimes(
                    latitude,
                    longitude,
                    highLatitudeAdjustment,
                    AsrJuristicMethod.HANAFI,
                    prayerTimeConvention,
                    timeFormat,
                    prayerManualCorrection
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
     * Fasting times are calculated based on Fajr and Maghrib prayers,
     * Sehri ends at Fajr start time, and Iftar begins at Maghrib time.
     */
    fun fetchCurrentMonthFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
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
                    prayerManualCorrection
                )
                val fastingTimes = prayerItems.map { extractFastingTimes(it) }
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch current yearly fasting times.
     * Fasting times are calculated based on Fajr and Maghrib prayers,
     * Sehri ends at Fajr start time, and Iftar begins at Maghrib time.
     */
    fun fetchCurrentYearFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
        prayerTimeConvention: PrayerTimeConvention = PrayerTimeConvention.KARACHI,
        timeFormat: TimeFormat = TimeFormat.HOUR_12,
        prayerManualCorrection: PrayerManualCorrection = PrayerManualCorrection(),
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
                    prayerManualCorrection
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

    private fun getDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection
    ): PrayerItem {

        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now

        return PrayerTimeRepository().getDailyPrayerTimes(
            latitude,
            longitude,
            calendar.time,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat,
            prayerManualCorrection
        )
    }

    private fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection
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
            prayerManualCorrection
        )
    }

    private fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat,
        prayerManualCorrection: PrayerManualCorrection
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
            prayerManualCorrection
        )
    }

    /**
     * Extracts fasting times (Sehri and Iftar) from the daily prayer times.
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


