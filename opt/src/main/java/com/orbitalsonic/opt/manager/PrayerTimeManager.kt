package com.orbitalsonic.opt.manager

import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.models.FastingItem
import com.orbitalsonic.opt.models.PrayerTimesItem
import com.orbitalsonic.opt.repository.PrayerTimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

/**
 * The `PrayerTimeManager` class is responsible for managing and providing prayer times.
 * It supports fetching daily, monthly, and yearly data based on geographic location and various calculation methods.
 */
class PrayerTimeManager {

    /**
     * Fetch daily prayer times.
     */
    fun fetchDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<PrayerTimesItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getDailyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch monthly prayer times.
     */
    fun fetchMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<List<PrayerTimesItem>>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getMonthlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch yearly prayer times.
     */
    fun fetchYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<List<List<PrayerTimesItem>>>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getYearlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                withContext(Dispatchers.Main) { callback(Result.success(result)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch daily fasting times.
     */
    fun fetchDailyFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<FastingItem>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerTimes = getDailyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                val fastingTimes = extractFastingTimes(prayerTimes)
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch monthly fasting times.
     */
    fun fetchMonthlyFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<FastingItem>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerTimesList = getMonthlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                val fastingTimes = prayerTimesList.map { extractFastingTimes(it) }
                withContext(Dispatchers.Main) { callback(Result.success(fastingTimes)) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(Result.failure(e)) }
            }
        }
    }

    /**
     * Fetch yearly fasting times.
     */
    fun fetchYearlyFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<List<FastingItem>>>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prayerTimesList = getYearlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
                )
                val fastingTimes = prayerTimesList.map { monthlyPrayerTimes ->
                    monthlyPrayerTimes.map { extractFastingTimes(it) }
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
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<PrayerTimesItem> {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now

        return PrayerTimeRepository().getDailyPrayerTimes(
            latitude, longitude, calendar.time, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
        )
    }

    private fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<PrayerTimesItem>> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return PrayerTimeRepository().getMonthlyPrayerTimes(
            latitude, longitude, currentMonth, currentYear, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
        )
    }

    private fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<List<PrayerTimesItem>>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return PrayerTimeRepository().getYearlyPrayerTimes(
            latitude, longitude, currentYear, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
        )
    }

    /**
     * Extracts fasting times (Sehri and Iftar) from the daily prayer times.
     */
    private fun extractFastingTimes(prayerTimes: List<PrayerTimesItem>): FastingItem {
        val fajrTime = prayerTimes.firstOrNull { it.prayerName == "Fajr" }?.prayerTime ?: "--:--"
        val maghribTime = prayerTimes.firstOrNull { it.prayerName == "Maghrib" }?.prayerTime ?: "--:--"

        return FastingItem(
            sehriTime = fajrTime,
            iftaarTime = maghribTime
        )
    }
}

