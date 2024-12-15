package com.orbitalsonic.offlineprayertime.repository

import android.util.Log
import com.orbitalsonic.offlineprayertime.enums.HighLatitudeAdjustment
import com.orbitalsonic.offlineprayertime.enums.JuristicMethod
import com.orbitalsonic.offlineprayertime.enums.OrganizationStandard
import com.orbitalsonic.offlineprayertime.enums.TimeFormat
import com.orbitalsonic.offlineprayertime.enums.TimeFrequency
import com.orbitalsonic.offlineprayertime.models.PrayerTimesItem
import com.orbitalsonic.offlineprayertime.utils.calculateAsrTime
import com.orbitalsonic.offlineprayertime.utils.calculateJulianDate
import com.orbitalsonic.offlineprayertime.utils.calculateMidDay
import com.orbitalsonic.offlineprayertime.utils.calculateTimeDifference
import com.orbitalsonic.offlineprayertime.utils.calculateTimeForAngle
import com.orbitalsonic.offlineprayertime.utils.convertTo12HourFormat
import com.orbitalsonic.offlineprayertime.utils.convertTo24HourFormat
import java.lang.Double.isNaN
import java.lang.Double.longBitsToDouble
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PrayerTimeRepository1 {

    private var prayerNames =
        listOf("Fajr", "Sunrise", "Zuhr", "Asr", "Sunset", "Maghrib", "Isha")
    private var fastingNames = listOf("Sehri", "Iftaar")
    private var invalidTime = "-----"

    private var highLatitudeAdjustment = HighLatitudeAdjustment.NONE
    private var juristicMethod = JuristicMethod.HANAFI
    private var organizationStandard = OrganizationStandard.KARACHI
    private var timeFormat = TimeFormat.HOUR_12
    private var timeFrequency = TimeFrequency.DAILY

    private var latitude = 0.0
    private var longitude = 0.0
    private var timeZone = 0.0
    private var julianDate = 0.0

    private var numIterations = 1 // number of iterations needed to compute times
    private var dhuhrMinutes = 0 // minutes after mid-day for Dhuhr

    // JAFARI
    val jValues = doubleArrayOf(16.0, 0.0, 4.0, 0.0, 14.0)

    // KARACHI
    val kValues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 18.0)

    // ISNA
    val iValues = doubleArrayOf(15.0, 1.0, 0.0, 0.0, 15.0)

    // MWL
    val mwValues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)

    // MAKKAH
    val mkValues = doubleArrayOf(18.5, 1.0, 0.0, 1.0, 90.0)

    // EGYPT
    val eValues = doubleArrayOf(19.5, 1.0, 0.0, 0.0, 17.5)

    // TEHRAN
    val tValues = doubleArrayOf(17.7, 0.0, 4.5, 0.0, 14.0)

    // CUSTOM
    var cValues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)

    fun fetchPrayerTimes(
        mLat: Double,
        mLon: Double,
        hlAdjustment: HighLatitudeAdjustment,
        jMethod: JuristicMethod,
        orgStandard: OrganizationStandard,
        tFormat: TimeFormat,
        tFrequency: TimeFrequency
    ): List<PrayerTimesItem> {

        latitude = mLat
        longitude = mLon
        highLatitudeAdjustment = hlAdjustment
        juristicMethod = jMethod
        organizationStandard = orgStandard
        timeFormat = tFormat
        timeFrequency = tFrequency


        val timeFormatter: SimpleDateFormat
        val prayerTimeList: ArrayList<PrayerTimesItem> = ArrayList()
        try {
            val defaultTz = TimeZone.getDefault()

            //Get NY calendar object with current date/time
            val defaultCalc = Calendar.getInstance(defaultTz)

            //Get offset from UTC, accounting for DST
            val defaultTzOffsetMs =
                defaultCalc[Calendar.ZONE_OFFSET] + defaultCalc[Calendar.DST_OFFSET]
            val timezone = defaultTzOffsetMs.toDouble() / (1000 * 60 * 60)

            val now = Date()
            val cal = Calendar.getInstance()
            cal.time = now

            var tFormat = ""

            tFormat = if (timeFormat == TimeFormat.HOUR_12) {
                "hh:mm aa"
            } else {
                "hh:mm";
            }
            timeFormatter = SimpleDateFormat(tFormat, Locale.US)

            val prayerTimes = getPrayerTimes(cal, timezone)

            for (i in prayerTimes.indices) {
                val prayerTimesItem = PrayerTimesItem("", "")
                prayerTimesItem.prayerName = prayerNames[i]

                val indexNamaz = timeFormatter.parse(prayerTimes[i]!!)
                var updateTime: String?

                if (i != 1 && i != 4) {
                    if (i == 0) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter.format(indexNamaz)
                            prayerTimesItem.prayerTime = updateTime
                        }
                        val indexNext = timeFormatter.parse(prayerTimes[2]!!)
                        if (indexNext != null) {
                            indexNext.minutes = indexNext.minutes
                        }
                    } else if (i == 2) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter.format(indexNamaz)
                            prayerTimesItem.prayerTime = updateTime
                        }
                        val indexNext = timeFormatter.parse(prayerTimes[3]!!)
                        if (indexNext != null) {
                            indexNext.minutes = indexNext.minutes
                        }
                    } else if (i == 3) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter.format(indexNamaz)
                            prayerTimesItem.prayerTime = updateTime
                        }
                        val indexNext = timeFormatter.parse(prayerTimes[5]!!)
                        if (indexNext != null) {
                            indexNext.minutes = indexNext.minutes
                        }
                    } else if (i == 5) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter.format(indexNamaz)
                            prayerTimesItem.prayerTime = updateTime
                        }
                        val indexNext = timeFormatter.parse(prayerTimes[6]!!)
                        if (indexNext != null) {
                            indexNext.minutes = indexNext.minutes
                        }
                    } else if (i == 6) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter.format(indexNamaz)
                            prayerTimesItem.prayerTime = updateTime
                        }
                        val indexNext = timeFormatter.parse(prayerTimes[0]!!)
                        if (indexNext != null) {
                            indexNext.minutes = indexNext.minutes
                        }

                        val nowFajr = Date()
                        val calFajr = Calendar.getInstance()
                        calFajr.time = nowFajr
                        calFajr.add(Calendar.DATE, 1)
                        val prayerTimeForFajr = getPrayerTimes(calFajr, timezone)
                        if (prayerTimeForFajr.size > 0) {
                            val indexNextFajr = timeFormatter.parse(prayerTimeForFajr[0]!!)
                            if (indexNextFajr != null) {
                                indexNextFajr.minutes = indexNextFajr.minutes
                            }
                        }
                    }

                }
                if (i == 1) {
                    if (indexNamaz != null) {
                        updateTime = timeFormatter.format(indexNamaz)
                        prayerTimesItem.prayerTime = updateTime
                    }

                } else if (i == 4) {
                    if (indexNamaz != null) {
                        indexNamaz.minutes = indexNamaz.minutes
                        updateTime = timeFormatter.format(indexNamaz)
                        prayerTimesItem.prayerTime = updateTime
                    }

                }
                prayerTimeList.add(prayerTimesItem)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return prayerTimeList
    }

    // return prayer times for a given date
    private fun getDatePrayerTimes(
        year: Int, month: Int, day: Int, tZone: Double
    ): ArrayList<String?> {
        timeZone = tZone
        julianDate = calculateJulianDate(year, month, day)
        val lonDiff = longitude / (15.0 * 24.0)
        julianDate -= lonDiff
        return computeDayTimes()
    }


    // return prayer times for a given date
    private fun getPrayerTimes(
        date: Calendar, tZone: Double
    ): ArrayList<String?> {
        var year = 0
        var month = 0
        var day = 0
        try {
            year = date[Calendar.YEAR]
            month = date[Calendar.MONTH]
            day = date[Calendar.DATE]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return getDatePrayerTimes(year, month + 1, day, tZone)
    }

    // set custom values for calculation parameters
    private fun setCustomParams(params: DoubleArray) {
        try {
            for (i in 0..4) {
                if (params[i] == -1.0) {
                    params[i] = getStandardValues(organizationStandard)[i]
                    cValues = params
                } else {
                    cValues[i] = params[i]
                }
            }
            organizationStandard = OrganizationStandard.CUSTOM
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // set the angle for calculating Fajr
    private fun setFajrAngle(angle: Double) {
        val params = doubleArrayOf(angle, -1.0, -1.0, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the angle for calculating Maghrib
    private fun setMaghribAngle(angle: Double) {
        val params = doubleArrayOf(-1.0, 0.0, angle, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the angle for calculating Isha
    private fun setIshaAngle(angle: Double) {
        val params = doubleArrayOf(-1.0, -1.0, -1.0, 0.0, angle)
        setCustomParams(params)
    }

    // set the minutes after Sunset for calculating Maghrib
    private fun setMaghribMinutes(minutes: Double) {
        val params = doubleArrayOf(-1.0, 1.0, minutes, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the minutes after Maghrib for calculating Isha
    private fun setIshaMinutes(minutes: Double) {
        val params = doubleArrayOf(-1.0, -1.0, -1.0, 1.0, minutes)
        setCustomParams(params)
    }

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private fun computeTimes(times: DoubleArray): DoubleArray {
        var fajr = 0.0
        var sunrise = 0.0
        var dhuhr = 0.0
        var asr = 0.0
        var sunset = 0.0
        var maghrib = 0.0
        var isha = 0.0
        try {
            val dTime = dayPortion(times)

            fajr = calculateTimeForAngle(
                180 - getStandardValues(organizationStandard)[0],
                dTime[0],
                latitude,
                julianDate
            )
            sunrise = calculateTimeForAngle(180 - 0.833, dTime[1], latitude, julianDate)
            dhuhr = calculateMidDay(dTime[2], julianDate)
            asr = calculateAsrTime(
                (1 + getAsrJuristicStep(juristicMethod)).toDouble(),
                dTime[3],
                latitude,
                julianDate
            )
            sunset = calculateTimeForAngle(0.833, dTime[4], latitude, julianDate)
            maghrib = calculateTimeForAngle(
                getStandardValues(organizationStandard)[2],
                dTime[5],
                latitude,
                julianDate
            )
            isha = calculateTimeForAngle(
                getStandardValues(organizationStandard)[4],
                dTime[6],
                latitude,
                julianDate
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return doubleArrayOf(fajr, sunrise, dhuhr, asr, sunset, maghrib, isha)
    }

    // compute prayer times at given julian date
    private fun computeDayTimes(): ArrayList<String?> {
        var times = DoubleArray(0) // default times
        try {
            times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0)

            for (i in 1..numIterations) {
                times = computeTimes(times)
            }
            times = adjustTimes(times)
            times = tuneTimes(times)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }

        return adjustTimesFormat(times)
    }

    // adjust times in a prayer time array
    private fun adjustTimes(times: DoubleArray): DoubleArray {
        var times = times
        try {
            for (i in times.indices) {
                times[i] += timeZone - longitude / 15
            }

            times[2] += dhuhrMinutes.toDouble() / 60 // Dhuhr
            if (getStandardValues(organizationStandard)[1] == 1.0) // Maghrib
            {
                times[5] = times[4] + getStandardValues(organizationStandard)[2] / 60
            }
            if (getStandardValues(organizationStandard)[3] == 1.0) // Isha
            {
                times[6] = times[5] + getStandardValues(organizationStandard)[4] / 60
            }

            if (highLatitudeAdjustment != HighLatitudeAdjustment.NONE) {
                times = adjustHighLatTimes(times)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return times
    }

    private fun adjustTimesFormat(times: DoubleArray): ArrayList<String?> {
        val result = ArrayList<String?>()

        try {
            when (timeFormat) {
                TimeFormat.FLOATING -> {
                    // Convert each time as a floating-point string
                    times.forEach { result.add(it.toString()) }
                }

                TimeFormat.HOUR_12, TimeFormat.HOUR_12_NS -> {
                    // Convert to 12-hour format (with or without AM/PM)
                    for (i in times.indices) {
                        result.add(
                            convertTo12HourFormat(
                                times[i],
                                timeFormat == TimeFormat.HOUR_12_NS
                            )
                        )
                    }
                }

                else -> {
                    // Default to 24-hour format
                    for (i in times.indices) {
                        result.add(convertTo24HourFormat(times[i]))
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }

        return result
    }


    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private fun adjustHighLatTimes(times: DoubleArray): DoubleArray {
        try {
            val nightTime = calculateTimeDifference(times[4], times[1]) // sunset to sunrise

            // Adjust Fajr
            val fajrDiff = nightPortion(
                getStandardValues(organizationStandard)[0]
            ) * nightTime

            if (isNaN(times[0]) || calculateTimeDifference(
                    times[0],
                    times[1]
                ) > fajrDiff
            ) {
                times[0] = times[1] - fajrDiff
            }

            // Adjust Isha
            val ishaAngle = if (getStandardValues(organizationStandard)[3] == 0.0)
                getStandardValues(organizationStandard)[4] else 18.0
            val ishaDiff = this.nightPortion(ishaAngle) * nightTime
            if (isNaN(times[6]) || calculateTimeDifference(
                    times[4],
                    times[6]
                ) > ishaDiff
            ) {
                times[6] = times[4] + ishaDiff
            }

            // Adjust Maghrib
            val maghribAngle = if (getStandardValues(organizationStandard)[1] == 0.0)
                getStandardValues(organizationStandard)[2] else 4.0
            val maghribDiff = nightPortion(maghribAngle) * nightTime
            if (isNaN(times[5]) || calculateTimeDifference(
                    times[4],
                    times[5]
                ) > maghribDiff
            ) {
                times[5] = times[4] + maghribDiff
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return times
    }

    // the night portion used for adjusting times in higher latitudes
    private fun nightPortion(angle: Double): Double {
        var calc = 0.0

        try {
            if (highLatitudeAdjustment == HighLatitudeAdjustment.ANGLE_BASED) calc = (angle) / 60.0
            else if (highLatitudeAdjustment == HighLatitudeAdjustment.MID_NIGHT) calc = 0.5
            else if (highLatitudeAdjustment == HighLatitudeAdjustment.ONE_SEVENTH) calc = 0.14286
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return calc
    }

    // convert hours to day portions
    private fun dayPortion(times: DoubleArray): DoubleArray {
        try {
            for (i in 0..6) {
                times[i] /= 24.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return times
    }

    private fun tuneTimes(times: DoubleArray): DoubleArray {
        try {
            for (i in times.indices) {
                times[i] = times[i]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return times
    }

    private fun getStandardValues(organizationStandard: OrganizationStandard): DoubleArray {
        when (organizationStandard) {
            OrganizationStandard.MAKKAH -> {
                return mkValues
            }

            OrganizationStandard.EGYPT -> {
                return eValues
            }

            OrganizationStandard.TEHRAN -> {
                return tValues
            }

            OrganizationStandard.JAFARI -> {
                return jValues
            }

            OrganizationStandard.KARACHI -> {
                return kValues
            }

            OrganizationStandard.ISNA -> {
                return iValues
            }

            OrganizationStandard.MWL -> {
                return mwValues
            }

            OrganizationStandard.CUSTOM -> {
                return cValues
            }
        }
    }

    // for compute the time of Asr (Shafii: step=1, Hanafi: step=2)
    private fun getAsrJuristicStep(asrJusristic: JuristicMethod): Int {
        return when (asrJusristic) {
            JuristicMethod.HANAFI -> 2
            JuristicMethod.SHAFII -> 1
        }
    }

}