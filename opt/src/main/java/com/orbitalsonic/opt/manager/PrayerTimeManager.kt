package com.orbitalsonic.opt.manager

import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.models.FastingItem
import com.orbitalsonic.opt.models.PrayerItem
import com.orbitalsonic.opt.models.PrayerTimes
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
     * Fetch today prayer times.
     */
    fun fetchTodayPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<PrayerItem>) -> Unit
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
     * Fetch current month prayer times.
     */
    fun fetchCurrentMonthPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<PrayerItem>>) -> Unit
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
     * Fetch current year prayer times.
     */
    fun fetchCurrentYearPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        callback: (Result<List<List<PrayerItem>>>) -> Unit
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
     * Fetch today fasting times.
     */
    fun fetchTodayFastingTimes(
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
                val prayerItem = getDailyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
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
     */
    fun fetchCurrentMonthFastingTimes(
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
                val prayerItems = getMonthlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
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
     */
    fun fetchCurrentYearFastingTimes(
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
                val prayerItems = getYearlyPrayerTimes(
                    latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
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
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): PrayerItem {

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
    ): List<PrayerItem> {
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
    ): List<List<PrayerItem>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return PrayerTimeRepository().getYearlyPrayerTimes(
            latitude, longitude, currentYear, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat
        )
    }

    /**
     * Extracts fasting times (Sehri and Iftar) from the daily prayer times.
     */
    private fun extractFastingTimes(prayerItem: PrayerItem): FastingItem {
        val fajrTime = prayerItem.prayerList.firstOrNull { it.prayerName == "Fajr" }?.prayerTime ?: "--:--"
        val maghribTime = prayerItem.prayerList.firstOrNull { it.prayerName == "Maghrib" }?.prayerTime ?: "--:--"

        return FastingItem(
            date = prayerItem.date,
            sehriTime = fajrTime,
            iftaarTime = maghribTime
        )
    }
}


