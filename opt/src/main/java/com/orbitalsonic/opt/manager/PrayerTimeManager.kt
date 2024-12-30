package com.orbitalsonic.opt.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.enums.TimeFrequency
import com.orbitalsonic.opt.models.FastingItem
import com.orbitalsonic.opt.models.PrayerTimesItem
import com.orbitalsonic.opt.repository.PrayerTimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * The `PrayerTimeManager` class is responsible for managing and providing prayer times.
 * It supports fetching daily, monthly, and yearly data based on geographic location and various calculation methods.
 */
class PrayerTimeManager {

    // LiveData to hold today's prayer times.
    private val _prayerTimeTodayLiveData = MutableLiveData<List<PrayerTimesItem>>()
    val prayerTimeTodayLiveData: LiveData<List<PrayerTimesItem>> = _prayerTimeTodayLiveData

    // LiveData to hold the prayer times for the current month.
    private val _prayerTimeMonthlyLiveData = MutableLiveData<List<List<PrayerTimesItem>>>()
    val prayerTimeMonthlyLiveData: LiveData<List<List<PrayerTimesItem>>> =
        _prayerTimeMonthlyLiveData

    // LiveData to hold the prayer times for the current year.
    private val _prayerTimeYearlyLiveData = MutableLiveData<List<List<List<PrayerTimesItem>>>>()
    val prayerTimeYearlyLiveData: LiveData<List<List<List<PrayerTimesItem>>>> = _prayerTimeYearlyLiveData

    // LiveData to hold today's fasting times.
    private val _fastingTimeTodayLiveData = MutableLiveData<FastingItem>()
    val fastingTimeTodayLiveData: LiveData<FastingItem> = _fastingTimeTodayLiveData

    // LiveData to hold the fasting times for the current month.
    private val _fastingTimeMonthlyLiveData = MutableLiveData<List<FastingItem>>()
    val fastingTimeMonthlyLiveData: LiveData<List<FastingItem>> = _fastingTimeMonthlyLiveData

    // LiveData to hold the fasting times for the current year.
    private val _fastingTimeYearlyLiveData = MutableLiveData<List<List<FastingItem>>>()
    val fastingTimeYearlyLiveData: LiveData<List<List<FastingItem>>> = _fastingTimeYearlyLiveData


    /**
     * Fetches prayer times based on the specified parameters and updates the corresponding LiveData.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param highLatitudeAdjustment Adjustment for high latitude locations.
     * @param juristicMethod The method of calculation based on Islamic jurisprudence.
     * @param organizationStandard The standard or organization used for calculations.
     * @param timeFormat The desired time format (e.g., 24-hour or 12-hour).
     * @param timeFrequency The frequency of the data to fetch (daily, monthly, or yearly).
     */
    fun fetchingPrayerTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        timeFrequency: TimeFrequency
    ) {
        when (timeFrequency) {
            TimeFrequency.DAILY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val pTimes = getDailyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    _prayerTimeTodayLiveData.postValue(pTimes)
                }
            }
            TimeFrequency.MONTHLY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val pTimes = getMonthlyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    _prayerTimeMonthlyLiveData.postValue(pTimes)
                }
            }
            TimeFrequency.YEARLY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val pTimes = getYearlyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    _prayerTimeYearlyLiveData.postValue(pTimes)
                }
            }
        }
    }

    /**
     * Fetches fasting times (Sehri and Iftar) based on the specified parameters and updates the corresponding LiveData.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param highLatitudeAdjustment Adjustment for high latitude locations.
     * @param juristicMethod The method of calculation based on Islamic jurisprudence.
     * @param organizationStandard The standard or organization used for calculations.
     * @param timeFormat The desired time format (e.g., 24-hour or 12-hour).
     * @param timeFrequency The frequency of the data to fetch (daily, monthly, or yearly).
     */
    fun fetchingFastingTimes(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat,
        timeFrequency: TimeFrequency
    ) {
        when (timeFrequency) {
            TimeFrequency.DAILY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val prayerTimes = getDailyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    val fastingItem = extractFastingTimes(prayerTimes)
                    _fastingTimeTodayLiveData.postValue(fastingItem)
                }
            }
            TimeFrequency.MONTHLY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val prayerTimesList = getMonthlyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    val fastingList = prayerTimesList.map { dailyPrayerTimes ->
                        extractFastingTimes(dailyPrayerTimes)
                    }
                    _fastingTimeMonthlyLiveData.postValue(fastingList)
                }
            }
            TimeFrequency.YEARLY -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val prayerTimesList = getYearlyPrayerTimes(
                        latitude = latitude,
                        longitude = longitude,
                        highLatitudeAdjustment = highLatitudeAdjustment,
                        juristicMethod = juristicMethod,
                        organizationStandard = organizationStandard,
                        timeFormat = timeFormat
                    )

                    val fastingList = prayerTimesList.map { monthlyPrayerTimes ->
                        monthlyPrayerTimes.map { dailyPrayerTimes ->
                            extractFastingTimes(dailyPrayerTimes)
                        }
                    }
                    _fastingTimeYearlyLiveData.postValue(fastingList)
                }
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
            latitude = latitude,
            longitude = longitude,
            date = calendar.time,
            highLatitudeAdjustment = highLatitudeAdjustment,
            juristicMethod = juristicMethod,
            organizationStandard = organizationStandard,
            timeFormat = timeFormat
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
            latitude = latitude,
            longitude = longitude,
            month = currentMonth,
            year = currentYear,
            highLatitudeAdjustment = highLatitudeAdjustment,
            juristicMethod = juristicMethod,
            organizationStandard = organizationStandard,
            timeFormat = timeFormat
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
            latitude = latitude,
            longitude = longitude,
            year = currentYear,
            highLatitudeAdjustment = highLatitudeAdjustment,
            juristicMethod = juristicMethod,
            organizationStandard = organizationStandard,
            timeFormat = timeFormat
        )
    }

    /**
     * Extracts fasting times (Sehri and Iftaar) from the daily prayer times.
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
