package com.orbitalsonic.sonicopt.repository

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.models.PrayerItem
import com.orbitalsonic.sonicopt.models.PrayerTimes
import com.orbitalsonic.sonicopt.utils.calculateAsrTime
import com.orbitalsonic.sonicopt.utils.calculateMidDay
import com.orbitalsonic.sonicopt.utils.calculateTimeDifference
import com.orbitalsonic.sonicopt.utils.calculateTimeForAngle
import com.orbitalsonic.sonicopt.utils.convertTo12HourFormat
import com.orbitalsonic.sonicopt.utils.convertTo12HourFormatNoSuffix
import com.orbitalsonic.sonicopt.utils.convertTo24HourFormat
import com.orbitalsonic.sonicopt.utils.convertToFloatingFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.floor

internal class PrayerTimeRepository {

    private val prayerNames = listOf("Fajr", "Sunrise", "Zuhr", "Asr", "Sunset", "Maghrib", "Isha")

    private var highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT
    private var asrJuristicMethod = AsrJuristicMethod.HANAFI
    private var prayerTimeConvention = PrayerTimeConvention.KARACHI
    private var timeFormat = TimeFormat.HOUR_12

    private var latitude = 0.0
    private var longitude = 0.0
    private var timeZone = 0.0
    private var julianDate = 0.0
    private var dhuhrMinutes = 0 // Minutes after midday for Zuhr calculation

    private val iterations = 1 // Number of iterations to compute times

    /**
     * Prayer Time Conventions
     * Pair First: Fajr Angle, Pair Second: Isha Angle
     * If Pair Second is -1, it represents 90 minutes after Maghrib for Isha.
     */
    private val standardAngleValues = mapOf(
        PrayerTimeConvention.KARACHI to Pair(18.0, 18.0),
        PrayerTimeConvention.ISNA to Pair(15.0, 15.0),
        PrayerTimeConvention.MWL to Pair(18.0, 17.0),
        PrayerTimeConvention.MAKKAH to Pair(18.5, -1.0),
        PrayerTimeConvention.EGYPT to Pair(19.5, 17.5),
        PrayerTimeConvention.TEHRAN to Pair(17.7, 14.0),
        PrayerTimeConvention.JAFFARI to Pair(16.0, 14.0),
        PrayerTimeConvention.DUBAI to Pair(18.2, 18.2),
        PrayerTimeConvention.MOONSIGHTING_COMMITTEE to Pair(18.0, 18.0),
        PrayerTimeConvention.KUWAIT to Pair(18.0, 17.5),
        PrayerTimeConvention.QATAR to Pair(18.0, -1.0),
        PrayerTimeConvention.SINGAPORE to Pair(20.0, 18.0),
        PrayerTimeConvention.GULF_REGION to Pair(19.5, -1.0),
        PrayerTimeConvention.FRANCE to Pair(12.0, 12.0),
        PrayerTimeConvention.TURKEY to Pair(18.0, 17.0),
        PrayerTimeConvention.RUSSIA to Pair(16.0, 15.0),
        PrayerTimeConvention.CUSTOM to Pair(9.0, 14.0)
    )

    private var customValues = Pair(9.0, 14.0)

    fun getDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat
    ): PrayerItem {
        initializeParameters(
            latitude,
            longitude,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat
        )
        return calculatePrayerTimesForDate(date)
    }

    fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat
    ): List<PrayerItem> {
        initializeParameters(
            latitude,
            longitude,
            highLatitudeAdjustment,
            asrJuristicMethod,
            prayerTimeConvention,
            timeFormat
        )
        return (1..getDaysInMonth(year, month)).map { day ->
            calculatePrayerTimesForDate(
                Calendar.getInstance().apply { set(year, month - 1, day) }.time
            )
        }
    }

    fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat
    ): List<List<PrayerItem>> {
        return (1..12).map { month ->
            getMonthlyPrayerTimes(
                latitude,
                longitude,
                month,
                year,
                highLatitudeAdjustment,
                asrJuristicMethod,
                prayerTimeConvention,
                timeFormat
            )
        }
    }

    private fun initializeParameters(
        latitude: Double,
        longitude: Double,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        asrJuristicMethod: AsrJuristicMethod,
        prayerTimeConvention: PrayerTimeConvention,
        timeFormat: TimeFormat
    ) {
        this.latitude = latitude
        this.longitude = longitude
        this.highLatitudeAdjustment = highLatitudeAdjustment
        this.asrJuristicMethod = asrJuristicMethod
        this.prayerTimeConvention = prayerTimeConvention
        this.timeFormat = timeFormat

        val defaultTimeZone = TimeZone.getDefault()
        val defaultTimeZoneOffsetHours =
            defaultTimeZone.getOffset(System.currentTimeMillis()).toDouble() / (1000 * 60 * 60)
        this.timeZone = defaultTimeZoneOffsetHours
    }

    private fun calculatePrayerTimesForDate(date: Date): PrayerItem {
        val calendar = Calendar.getInstance().apply { time = date }
        julianDate = calculateJulianDate(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DATE]
        ) - (longitude / (15.0 * 24.0))

        val rawTimes = computeRawPrayerTimes()
        val adjustedTimes = adjustTimes(rawTimes)
        val prayerTimesList = formatPrayerTimes(adjustedTimes)
        return PrayerItem(date.time, prayerTimesList)
    }

    private fun computeRawPrayerTimes(): DoubleArray {
        var times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0)
        repeat(iterations) {
            times = computeTimes(times)
        }
        return times
    }

    private fun computeTimes(times: DoubleArray): DoubleArray {
        val dayPortions = dayPortion(times)
        val standardAngleParams = getCalculationParameters()

        val fajrTime = calculateTimeForAngle(180 - standardAngleParams.first, dayPortions[0], latitude, julianDate)
        val sunriseTime = calculateTimeForAngle(180 - 0.833, dayPortions[1], latitude, julianDate)
        val zuhrTime = calculateMidDay(dayPortions[2], julianDate)
        val asrTime = calculateAsrTime(
            (getAsrJuristicStep(asrJuristicMethod)).toDouble(),
            dayPortions[3],
            latitude,
            julianDate
        )
        val sunsetTime = calculateTimeForAngle(0.833, dayPortions[4], latitude, julianDate)
        val maghribTime = calculateTimeForAngle(0.833, dayPortions[5], latitude, julianDate)


        val ishaTime = if (standardAngleParams.second == -1.0){
           // Angle -1, it represents 90 minutes after Maghrib for Isha.
            maghribTime + 1.5
        }else{
            calculateTimeForAngle(standardAngleParams.second, dayPortions[6], latitude, julianDate)
        }

        // Fajr, Sunrise, Zuhr, Asr, Sunset, Maghrib, Isha respectively
        return doubleArrayOf(
            fajrTime,
            sunriseTime,
            zuhrTime,
            asrTime,
            sunsetTime,
            maghribTime,
            ishaTime
        )
    }

    private fun adjustTimes(times: DoubleArray): DoubleArray {
        val adjustedTimes = times.mapIndexed { index, time ->
            time + timeZone - longitude / 15.0 + if (index == 2) dhuhrMinutes / 60.0 else 0.0
        }.toDoubleArray()

        return if (highLatitudeAdjustment != HighLatitudeAdjustment.NO_ADJUSTMENT) {
            adjustHighLatitudeTimes(adjustedTimes)
        } else {
            adjustedTimes
        }
    }

    private fun adjustHighLatitudeTimes(times: DoubleArray): DoubleArray {
        val nightTime = calculateTimeDifference(times[4], times[1])
        val fajrDiff = nightPortion(getCalculationParameters().first) * nightTime
        val ishaDiff = nightPortion(getCalculationParameters().second) * nightTime

        if (calculateTimeDifference(times[0], times[1]) > fajrDiff) {
            times[0] = times[1] - fajrDiff
        }
        if (calculateTimeDifference(times[4], times[6]) > ishaDiff) {
            times[6] = times[4] + ishaDiff
        }
        return times
    }

    private fun formatPrayerTimes(times: DoubleArray): List<PrayerTimes> {
        return prayerNames.mapIndexed { index, name ->
            PrayerTimes(
                name,
                when (timeFormat) {
                    TimeFormat.HOUR_12 -> convertTo12HourFormat(times[index])
                    TimeFormat.HOUR_12_NS -> convertTo12HourFormatNoSuffix(times[index])
                    TimeFormat.HOUR_24 -> convertTo24HourFormat(times[index])
                    TimeFormat.FLOATING -> convertToFloatingFormat(times[index])
                }
            )
        }
    }

    private fun getCalculationParameters(): Pair<Double, Double> {
        return standardAngleValues[prayerTimeConvention] ?: customValues
    }

    private fun nightPortion(angle: Double): Double {
        return when (highLatitudeAdjustment) {
            HighLatitudeAdjustment.TWILIGHT_ANGLE -> angle / 60.0
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

    // Compute the time of Asr (Shafii: step=1, Hanafi: step=2)
    private fun getAsrJuristicStep(method: AsrJuristicMethod): Int = when (method) {
        AsrJuristicMethod.SHAFI -> 1
        AsrJuristicMethod.HANAFI -> 2
    }

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

