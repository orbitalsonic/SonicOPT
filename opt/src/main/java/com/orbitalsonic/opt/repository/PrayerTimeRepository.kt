package com.orbitalsonic.opt.repository

import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.models.PrayerTimesItem
import com.orbitalsonic.opt.utils.calculateAsrTime
import com.orbitalsonic.opt.utils.calculateMidDay
import com.orbitalsonic.opt.utils.calculateTimeDifference
import com.orbitalsonic.opt.utils.calculateTimeForAngle
import com.orbitalsonic.opt.utils.convertTo12HourFormat
import com.orbitalsonic.opt.utils.convertTo24HourFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.floor

internal class PrayerTimeRepository {

    // Names for prayers and fasting
    private val prayerNames = listOf("Fajr", "Sunrise", "Zuhr", "Asr", "Sunset", "Maghrib", "Isha")

    // Calculation parameters
    private var highLatitudeAdjustment = HighLatitudeAdjustment.NONE
    private var juristicMethod = JuristicMethod.HANAFI
    private var organizationStandard = OrganizationStandard.KARACHI
    private var timeFormat = TimeFormat.HOUR_12

    private var latitude = 0.0
    private var longitude = 0.0
    private var timeZone = 0.0
    private var julianDate = 0.0
    private var dhuhrMinutes = 0 // Minutes after midday for Zuhr calculation

    private val iterations = 1 // Number of iterations to compute times

    // Organization-specific parameters
    private val standardValues = mapOf(
        OrganizationStandard.KARACHI to doubleArrayOf(18.0, 1.0, 0.0, 0.0, 18.0),
        OrganizationStandard.ISNA to doubleArrayOf(15.0, 1.0, 0.0, 0.0, 15.0),
        OrganizationStandard.MWL to doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0),
        OrganizationStandard.MAKKAH to doubleArrayOf(18.5, 1.0, 0.0, 1.0, 90.0),
        OrganizationStandard.EGYPT to doubleArrayOf(19.5, 1.0, 0.0, 0.0, 17.5),
        OrganizationStandard.TEHRAN to doubleArrayOf(17.7, 0.0, 4.5, 0.0, 14.0),
        OrganizationStandard.JAFARI to doubleArrayOf(16.0, 0.0, 4.0, 0.0, 14.0)
    )
    private var customValues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)

    // Public function to fetch daily prayer times
    fun getDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<PrayerTimesItem> {
        initializeParameters(latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat)
        return calculatePrayerTimesForDate(date)
    }

    // Public function to fetch monthly prayer times
    fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<PrayerTimesItem>> {
        initializeParameters(latitude, longitude, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat)
        return (1..getDaysInMonth(year, month)).map { day ->
            calculatePrayerTimesForDate(Calendar.getInstance().apply { set(year, month - 1, day) }.time)
        }
    }

    // Public function to fetch yearly prayer times
    fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<List<PrayerTimesItem>>> {
        return (1..12).map { month ->
            getMonthlyPrayerTimes(latitude, longitude, month, year, highLatitudeAdjustment, juristicMethod, organizationStandard, timeFormat)
        }
    }

    // Initialize parameters for calculations
    private fun initializeParameters(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ) {
        this.latitude = latitude
        this.longitude = longitude
        this.highLatitudeAdjustment = highLatitudeAdjustment
        this.juristicMethod = juristicMethod
        this.organizationStandard = organizationStandard
        this.timeFormat = timeFormat

        // Get the default time zone and calculate its offset in hours
        val defaultTimeZone = TimeZone.getDefault()
        val defaultTimeZoneOffsetHours = defaultTimeZone.getOffset(System.currentTimeMillis()).toDouble() / (1000 * 60 * 60)
        this.timeZone = defaultTimeZoneOffsetHours
    }

    // Calculate prayer times for a specific date
    private fun calculatePrayerTimesForDate(date: Date): List<PrayerTimesItem> {
        val calendar = Calendar.getInstance().apply { time = date }
        julianDate = calculateJulianDate(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DATE]
        ) - (longitude / (15.0 * 24.0))

        val rawTimes = computeRawPrayerTimes()
        val adjustedTimes = adjustTimes(rawTimes)
        return formatPrayerTimes(adjustedTimes)
    }

    // Compute raw prayer times
    private fun computeRawPrayerTimes(): DoubleArray {
        var times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0) // Approximate starting points
        repeat(iterations) {
            times = computeTimes(times)
        }
        return times
    }

    private fun computeTimes(times: DoubleArray): DoubleArray {
        val dayPortions = dayPortion(times)
        val standardParams = getCalculationParameters()
        return doubleArrayOf(
            calculateTimeForAngle(180 - standardParams[0], dayPortions[0], latitude, julianDate), // Fajr
            calculateTimeForAngle(180 - 0.833, dayPortions[1], latitude, julianDate),            // Sunrise
            calculateMidDay(dayPortions[2], julianDate),                                         // Zuhr
            calculateAsrTime((1 + getAsrJuristicStep(juristicMethod)).toDouble(), dayPortions[3], latitude, julianDate), // Asr
            calculateTimeForAngle(0.833, dayPortions[4], latitude, julianDate),                 // Sunset
            calculateTimeForAngle(standardParams[2], dayPortions[5], latitude, julianDate),     // Maghrib
            calculateTimeForAngle(standardParams[4], dayPortions[6], latitude, julianDate)      // Isha
        )
    }

    private fun adjustTimes(times: DoubleArray): DoubleArray {
        val adjustedTimes = times.mapIndexed { index, time ->
            time + timeZone - longitude / 15.0 + if (index == 2) dhuhrMinutes / 60.0 else 0.0
        }.toDoubleArray()

        return if (highLatitudeAdjustment != HighLatitudeAdjustment.NONE) {
            adjustHighLatitudeTimes(adjustedTimes)
        } else {
            adjustedTimes
        }
    }

    private fun adjustHighLatitudeTimes(times: DoubleArray): DoubleArray {
        val nightTime = calculateTimeDifference(times[4], times[1]) // Night duration
        val fajrDiff = nightPortion(getCalculationParameters()[0]) * nightTime
        val ishaDiff = nightPortion(getCalculationParameters()[4]) * nightTime

        if (calculateTimeDifference(times[0], times[1]) > fajrDiff) {
            times[0] = times[1] - fajrDiff
        }
        if (calculateTimeDifference(times[4], times[6]) > ishaDiff) {
            times[6] = times[4] + ishaDiff
        }
        return times
    }

    private fun formatPrayerTimes(times: DoubleArray): List<PrayerTimesItem> {
        return prayerNames.mapIndexed { index, name ->
            PrayerTimesItem(
                name,
                when (timeFormat) {
                    TimeFormat.HOUR_12 -> convertTo12HourFormat(times[index])
                    TimeFormat.HOUR_12_NS -> convertTo12HourFormat(times[index], false)
                    else -> convertTo24HourFormat(times[index])
                }
            )
        }
    }

    private fun getCalculationParameters(): DoubleArray {
        return standardValues[organizationStandard] ?: customValues
    }

    private fun nightPortion(angle: Double): Double {
        return when (highLatitudeAdjustment) {
            HighLatitudeAdjustment.ANGLE_BASED -> angle / 60.0
            HighLatitudeAdjustment.MID_NIGHT -> 0.5
            HighLatitudeAdjustment.ONE_SEVENTH -> 1.0 / 7.0
            else -> 0.0
        }
    }

    private fun dayPortion(times: DoubleArray): DoubleArray {
        return times.map { time -> time / 24.0 }.toDoubleArray()
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getAsrJuristicStep(method: JuristicMethod): Int = if (method == JuristicMethod.HANAFI) 2 else 1

    private fun calculateJulianDate(year: Int, month: Int, day: Int): Double {
        return if (month <= 2) {
            val newYear = year - 1
            val newMonth = month + 12
            julianDate(newYear, newMonth, day)
        } else {
            julianDate(year, month, day)
        }
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        return (367.0 * year) - floor((7 * (year + floor((month + 9) / 12.0))) / 4) + floor(
            ((275 * month) / 9).toDouble()
        ) + day + 1721013.5
    }
}
