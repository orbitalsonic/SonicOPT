package com.orbitalsonic.offlineprayertime.repository

import android.util.Log
import com.orbitalsonic.offlineprayertime.utils.calculateJulianDate
import com.orbitalsonic.offlineprayertime.utils.convertTo12HourFormat
import com.orbitalsonic.offlineprayertime.utils.convertTo24HourFormat
import java.util.Calendar
import java.util.Objects

class PrayerTimeRepository {

    private var prayerNames = listOf("Fajr","Sunrise", "Dahwa-e-Kubra", "Zuhr", "Asr","Sunset", "Maghrib", "Isha")
    private var fastingNames = listOf("Sehri", "Iftaar")
    private var invalidTime = "-----"

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
    val cValues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)

    // return prayer times for a given date
    private fun getDatePrayerTimes(
        year: Int, month: Int, day: Int,
        latitude: Double, longitude: Double, tZone: Double
    ): ArrayList<String?> {
        this.setLat(latitude)
        this.setLng(longitude)
        this.setTimeZone(tZone)
        this.setJDate(calculateJulianDate(year, month, day))
        val lonDiff = longitude / (15.0 * 24.0)
        this.setJDate(this.getJDate() - lonDiff)
        return computeDayTimes()
    }


    // return prayer times for a given date
    private fun getPrayerTimes(
        date: Calendar, latitude: Double,
        longitude: Double, tZone: Double
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

        return getDatePrayerTimes(year, month + 1, day, latitude, longitude, tZone)
    }

    // set custom values for calculation parameters
    private fun setCustomParams(params: DoubleArray) {
        try {
            for (i in 0..4) {
                if (params[i] == -1.0) {
                    params[i] = Objects.requireNonNull(methodParams!![getCalcMethod()])!![i]
                    methodParams!![CUSTOM] = params
                } else {
                    Objects.requireNonNull(methodParams!![CUSTOM])!![i] = params[i]
                }
            }
            setCalcMethod(CUSTOM)
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
        var Fajr = 0.0
        var Sunrise = 0.0
        var Dhuhr = 0.0
        var Asr = 0.0
        var Sunset = 0.0
        var Maghrib = 0.0
        var Isha = 0.0
        try {
            val t = dayPortion(times)

            Fajr = this.calculateTimeForAngle(
                180 - Objects.requireNonNull(methodParams!![getCalcMethod()])!![0],
                t[0]
            )

            Sunrise = this.calculateTimeForAngle(180 - 0.833, t[1])

            Dhuhr = this.calculateMidDay(t[2])
            Asr = this.calculateAsrTime(
                (1 + this.getAsrJuristic()).toDouble(),
                t[3]
            )
            Sunset = this.calculateTimeForAngle(0.833, t[4])

            Maghrib = this.calculateTimeForAngle(
                Objects.requireNonNull(methodParams!![getCalcMethod()])!![2],
                t[5]
            )
            Isha = this.calculateTimeForAngle(
                Objects.requireNonNull(methodParams!![getCalcMethod()])!![4],
                t[6]
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return doubleArrayOf(Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha)
    }

    // compute prayer times at given julian date
    private fun computeDayTimes(): ArrayList<String?> {
        var times = DoubleArray(0) // default times
        try {
            times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0)

            for (i in 1..this.getNumIterations()) {
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
                times[i] += this.getTimeZone() - this.getLng() / 15
            }

            times[2] += getDhuhrMinutes().toDouble() / 60 // Dhuhr
            if (Objects.requireNonNull(methodParams!![getCalcMethod()])!![1] == 1.0) // Maghrib
            {
                times[5] = times[4] + Objects.requireNonNull(
                    methodParams!![getCalcMethod()]
                )!![2] / 60
            }
            if (Objects.requireNonNull(methodParams!![getCalcMethod()])!![3] == 1.0) // Isha
            {
                times[6] = times[5] + Objects.requireNonNull(
                    methodParams!![getCalcMethod()]
                )!![4] / 60
            }

            if (this.getAdjustHighLats() != NONE) {
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
            when (this.getTimeFormat()) {
                FLOATING -> {
                    // Convert each time as a floating-point string
                    times.forEach { result.add(it.toString()) }
                }
                TIME_12, TIME_12_NS -> {
                    // Convert to 12-hour format (with or without AM/PM)
                    for (i in times.indices) {
                        result.add(convertTo12HourFormat(times[i], this.getTimeFormat() == TIME_12_NS))
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
            val FajrDiff = nightPortion(
                Objects.requireNonNull(
                    methodParams!![getCalcMethod()]
                )!![0]
            ) * nightTime

            if (java.lang.Double.isNaN(times[0]) || calculateTimeDifference(times[0], times[1]) > FajrDiff) {
                times[0] = times[1] - FajrDiff
            }

            // Adjust Isha
            val IshaAngle = if ((Objects.requireNonNull(
                    methodParams!![getCalcMethod()]
                )!![3] == 0.0)
            ) Objects.requireNonNull(methodParams!![getCalcMethod()])!![4] else 18.0
            val IshaDiff = this.nightPortion(IshaAngle) * nightTime
            if (java.lang.Double.isNaN(times[6]) || this.calculateTimeDifference(times[4], times[6]) > IshaDiff) {
                times[6] = times[4] + IshaDiff
            }

            // Adjust Maghrib
            val MaghribAngle = if ((Objects.requireNonNull(
                    methodParams!![getCalcMethod()]
                )!![1] == 0.0)
            ) Objects.requireNonNull(
                methodParams!![getCalcMethod()]
            )!![2] else 4.0
            val MaghribDiff = nightPortion(MaghribAngle) * nightTime
            if (java.lang.Double.isNaN(times[5]) || this.calculateTimeDifference(
                    times[4],
                    times[5]
                ) > MaghribDiff
            ) {
                times[5] = times[4] + MaghribDiff
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
            if (adjustHighLats == ANGLE_BASED) calc = (angle) / 60.0
            else if (adjustHighLats == MID_NIGHT) calc = 0.5
            else if (adjustHighLats == ONE_SEVENTH) calc = 0.14286
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

    // Tune timings for adjustments
    // Set time offsets
    private fun tune(offsetTimes: IntArray) {
        try {
            for (i in offsetTimes.indices) {
                // offsetTimes length
                // should be 7 in order
                // of Fajr, Sunrise,
                // Dhuhr, Asr, Sunset,
                // Maghrib, Isha
                offsets!![i] = offsetTimes[i]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun tuneTimes(times: DoubleArray): DoubleArray {
        try {
            for (i in times.indices) {
                times[i] = times[i] + offsets!![i] / 60.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return times
    }
}