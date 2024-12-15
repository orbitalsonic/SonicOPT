package com.orbitalsonic.offlineprayertime.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orbitalsonic.offlineprayertime.enums.HighLatitudeAdjustment
import com.orbitalsonic.offlineprayertime.enums.JuristicMethod
import com.orbitalsonic.offlineprayertime.enums.OrganizationStandard
import com.orbitalsonic.offlineprayertime.enums.TimeFormat
import com.orbitalsonic.offlineprayertime.enums.TimeFrequency
import com.orbitalsonic.offlineprayertime.models.PrayerTimesItem
import com.orbitalsonic.offlineprayertime.repository.PrayerTimeRepository
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

    private val prayerTimeRepository = PrayerTimeRepository()

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

        return prayerTimeRepository.getDailyPrayerTimes(
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

        return prayerTimeRepository.getMonthlyPrayerTimes(
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

        return prayerTimeRepository.getYearlyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            year = currentYear,
            highLatitudeAdjustment = highLatitudeAdjustment,
            juristicMethod = juristicMethod,
            organizationStandard = organizationStandard,
            timeFormat = timeFormat
        )
    }
}
