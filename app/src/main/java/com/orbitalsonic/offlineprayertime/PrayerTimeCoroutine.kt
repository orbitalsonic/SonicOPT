package com.hypersoft.offlineprayertimes.managers.threads

import android.util.Log
import com.hypersoft.offlineprayertimes.managers.sharedPref.SharedPrefManager
import com.hypersoft.offlineprayertimes.models.PrayerTimeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

abstract class PrayerTimeCoroutine() : CoroutineScope {

    override val coroutineContext: CoroutineContext get() = Dispatchers.Main

    // Calculation Methods
    private val MAKKAH: Int = 4 // Umm al-Qura, MAKKAH
    private val EGYPT: Int = 5 // Egyptian General Authority of Survey
    private val CUSTOM: Int = 7 // CUSTOM Setting
    private val TEHRAN: Int = 6 // Institute of Geophysics, University of TEHRAN
    private val JAFARI: Int = 0 // Ithna Ashari
    private val KARACHI: Int = 1 // University of Islamic Sciences, KARACHI
    private val ISNA: Int = 2 // Islamic Society of North America (ISNA)
    private val MWL: Int = 3 // Muslim World League (MWL)

    // Adjusting Methods for Higher Latitudes
    private val NONE: Int = 0 // No adjustment
    private val MID_NIGHT: Int = 1 // middle of night
    private val ONE_SEVENTH: Int = 2 // 1/7th of night
    private val ANGLE_BASED: Int = 3 // angle/60th of night

    // Time Formats
    private val TIME_24: Int = 0 // 24-hour format
    private val TIME_12: Int = 1 // 12-hour format
    private val TIME_12_NS: Int = 2 // 12-hour format with no suffix
    private val FLOATING: Int = 3 // floating point number

    // Juristic Methods
    private val SHAFII: Int = 0 // SHAFII
    private val HANAFI: Int = 1 // HANAFI (standard)

    // ---------------------- Global Variables --------------------
    private var calcMethod = 0 // caculation method
    private var asrJuristic = 0 // Juristic method for Asr
    private var dhuhrMinutes = 0 // minutes after mid-day for Dhuhr
    private var adjustHighLats = 0 // adjusting method for higher latitudes
    private var timeFormat = 0 // time format
    private var lat = 0.0 // latitude
    private var lng = 0.0 // longitude
    private var timeZone = 0.0 // time-zone
    private var JDate = 0.0 // Julian date

    // Time Names
    private var timeNames: ArrayList<String>? = null
    private var InvalidTime: String? = null // The string used for invalid times

    // --------------------- Technical Settings --------------------
    private var numIterations = 0 // number of iterations needed to compute times

    // ------------------- Calc Method Parameters --------------------
    private var methodParams: HashMap<Int, DoubleArray>? = null
    private var prayerTimeList: ArrayList<PrayerTimeModel>? = ArrayList()

    /*
     * this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);
     *
     * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
     * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
     * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter value
     * (in angle or minutes)
     */
    //    private double[] prayerTimesCurrent;
    private var offsets: IntArray? = null

    //    Calendar calendarAlarm;
    private var timeFormatter: SimpleDateFormat? = null

    //    String[] numberList;
    private var nextDayFajrTime: String = ""
    private var mainSharedPref: SharedPrefManager? = null

    constructor(
        mainSharedPref: SharedPrefManager
    ) : this() {
        this.mainSharedPref = mainSharedPref
    }

    open fun onPostExecute(prayerTimeList: ArrayList<PrayerTimeModel>?, nextDayFajrTime: String?) {}
    open fun onPreExecute() {}

    fun executeCoroutine(): Job {
        return launch {
            onPreExecute()
            onPrayerDataInit()
            doInBackground() // runs in background thread without blocking the Main Thread
            val newPrayList = prayerTimeList?.let { ArrayList(it) }
            if (newPrayList?.size == 7 &&
                (nextDayFajrTime.isNotEmpty())
            ) {
                onPostExecute(newPrayList, nextDayFajrTime)
            } else {
                onPostExecute(null, null)
            }
        }
    }

    private fun onPrayerDataInit() {

        try {
            // Initialize vars
//        this.setCalcMethod(0);
//        this.setAsrJuristic(0);
//        this.setDhuhrMinutes(0);
//        this.setAdjustHighLats(1);
//        this.setTimeFormat(0);

            // Time Names

            timeNames = ArrayList()
            timeNames!!.add("Fajr")
            timeNames!!.add("Sunrise")
            timeNames!!.add("Dhuhr")
            timeNames!!.add("Asr")
            timeNames!!.add("Sunset")
            timeNames!!.add("Maghrib")
            timeNames!!.add("Isha")

            InvalidTime = "-----" // The string used for invalid times

            // --------------------- Technical Settings --------------------
            this.setNumIterations(1) // number of iterations needed to compute

            // times

            // ------------------- Calc Method Parameters --------------------

            // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
            offsets = IntArray(7)
            offsets!![0] = 0
            offsets!![1] = 0
            offsets!![2] = 0
            offsets!![3] = 0
            offsets!![4] = 0
            offsets!![5] = 0
            offsets!![6] = 0
            tune(offsets!!)
            /*
             * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
             * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
             * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter
             * value (in angle or minutes)
             */
            methodParams = HashMap()

            // JAFARI
            val Jvalues = doubleArrayOf(16.0, 0.0, 4.0, 0.0, 14.0)
            methodParams!![JAFARI] = Jvalues

            // KARACHI
            val Kvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 18.0)
            methodParams!![KARACHI] = Kvalues

            // ISNA
            val Ivalues = doubleArrayOf(15.0, 1.0, 0.0, 0.0, 15.0)
            methodParams!![ISNA] = Ivalues

            // MWL
            val MWvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)
            methodParams!![MWL] = MWvalues

            // MAKKAH
            val MKvalues = doubleArrayOf(18.5, 1.0, 0.0, 1.0, 90.0)
            methodParams!![MAKKAH] = MKvalues

            // EGYPT
            val Evalues = doubleArrayOf(19.5, 1.0, 0.0, 0.0, 17.5)
            methodParams!![EGYPT] = Evalues

            // TEHRAN
            val Tvalues = doubleArrayOf(17.7, 0.0, 4.5, 0.0, 14.0)
            methodParams!![TEHRAN] = Tvalues

            // CUSTOM
            val Cvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)
            methodParams!![CUSTOM] = Cvalues

            Log.i("PrayerTime", "main: Inside Cons New = 222")
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }

        prayerTimeList?.clear()

    }

    private suspend fun doInBackground() =
        withContext(Dispatchers.IO) {
            // to run code in Background Thread

            Log.d("prayersData", "doInBackground: All Prayer = $isActive")

            if (isActive) {
                if (mainSharedPref != null) {
                    Log.d("prayersData", "doInBackground: All 2 Prayer = $isActive")
                    refreshPrayerTimes(mainSharedPref!!)
                }
            }
            return@withContext

        }

    // ---------------------- Trigonometric Functions -----------------------
    // Normalize an angle in degrees to the range [0, 360)
    private fun normalizeAngle(angle: Double): Double {
        val normalized = angle % 360.0
        return if (normalized < 0) normalized + 360.0 else normalized
    }

    // Normalize hours to the range [0, 24)
    private fun normalizeHour(hour: Double): Double {
        val normalized = hour % 24.0
        return if (normalized < 0) normalized + 24.0 else normalized
    }

    // Convert radians to degrees
    private fun radiansToDegrees(radians: Double): Double {
        return ((radians * 180.0) / Math.PI)
    }

    // Convert degrees to radians
    private fun degreesToRadians(degrees: Double): Double {
        return ((degrees * Math.PI) / 180.0)
    }

    // Sine of an angle in degrees
    private fun sinDegrees(degrees: Double): Double {
        return sin(degreesToRadians(degrees))
    }

    // Cosine of an angle in degrees
    private fun cosDegrees(degrees: Double): Double {
        return cos(degreesToRadians(degrees))
    }

    // Tangent of an angle in degrees
    private fun tanDegrees(degrees: Double): Double {
        return tan(degreesToRadians(degrees))
    }

    // Arcsine in degrees
    private fun arcsinDegrees(value: Double): Double {
        return radiansToDegrees(asin(value))
    }

    // Arccosine in degrees
    private fun arccosDegrees(value: Double): Double {
        return radiansToDegrees(acos(value))
    }

    // Arctangent in degrees
    private fun arctanDegrees(value: Double): Double {
        return radiansToDegrees(atan(value))
    }

    // Arctangent2 (two-argument arctangent) in degrees
    private fun arctan2Degrees(y: Double, x: Double): Double {
        return radiansToDegrees(atan2(y, x))
    }

    // Arccotangent in degrees
    private fun arccotDegrees(value: Double): Double {
        return radiansToDegrees(atan2(1.0, value))
    }

    // ---------------------- Time-Zone Functions -----------------------

    // Compute local time zone offset for the current date (in hours)
    private fun getLocalTimeZone(): Double {
        val timeZone = TimeZone.getDefault()
        return timeZone.rawOffset / 3600000.0 // Convert milliseconds to hours
    }

    // Compute the base time zone offset of the system (in hours)
    private fun getBaseTimeZone(): Double {
        val timeZone = TimeZone.getDefault()
        return timeZone.rawOffset / 3600000.0 // Convert milliseconds to hours
    }

    // Detect daylight saving time offset for the current date (in milliseconds)
    private fun getDaylightSavingOffset(): Double {
        val timeZone = TimeZone.getDefault()
        return timeZone.dstSavings.toDouble()
    }

    // ---------------------- Julian Date Functions -----------------------

    // Calculate Julian date from a calendar date
    private fun calculateJulianDate(year: Int, month: Int, day: Int): Double {
        var adjustedYear = year
        var adjustedMonth = month

        if (month <= 2) {
            adjustedYear -= 1
            adjustedMonth += 12
        }

        val A = floor(adjustedYear / 100.0)
        val B = 2 - A + floor(A / 4.0)

        return floor(365.25 * (adjustedYear + 4716)) +
                floor(30.6001 * (adjustedMonth + 1)) +
                day + B - 1524.5
    }

    // Convert a calendar date to Julian date using epoch reference
    private fun calculateJulianDateFromEpoch(year: Int, month: Int, day: Int): Double {
        val J1970 = 2440587.5 // Julian date for Unix epoch (Jan 1, 1970)
        return try {
            val date = Date(year - 1900, month - 1, day) // Date(year offset by 1900, month 0-indexed)
            val milliseconds = date.time.toDouble()
            val daysSinceEpoch = milliseconds / (1000 * 60 * 60 * 24) // Convert ms to days
            J1970 + daysSinceEpoch
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error calculating Julian date from epoch: $e", e)
            0.0 // Return 0.0 if an exception occurs
        }
    }

    // ---------------------- Calculation Functions -----------------------

    // Compute declination angle of the sun and equation of time
    private fun calculateSunPosition(julianDate: Double): DoubleArray {
        return try {
            val daysSinceEpoch = julianDate - 2451545.0
            val meanAnomaly = normalizeAngle(357.529 + 0.98560028 * daysSinceEpoch)
            val meanLongitude = normalizeAngle(280.459 + 0.98564736 * daysSinceEpoch)
            val apparentLongitude = normalizeAngle(
                meanLongitude + 1.915 * sinDegrees(meanAnomaly) + 0.020 * sinDegrees(2 * meanAnomaly)
            )
            val obliquity = 23.439 - 0.00000036 * daysSinceEpoch

            // Declination angle
            val declination = arcsinDegrees(sinDegrees(obliquity) * sinDegrees(apparentLongitude))

            // Right ascension (in hours)
            var rightAscension = arctan2Degrees(
                cosDegrees(obliquity) * sinDegrees(apparentLongitude),
                cosDegrees(apparentLongitude)
            ) / 15.0
            rightAscension = normalizeHour(rightAscension)

            // Equation of time
            val equationOfTime = meanLongitude / 15.0 - rightAscension

            doubleArrayOf(declination, equationOfTime)
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in calculateSunPosition: $e", e)
            doubleArrayOf(0.0, 0.0) // Return default values in case of error
        }
    }

    // Compute the equation of time
    private fun calculateEquationOfTime(julianDate: Double): Double {
        return calculateSunPosition(julianDate)[1]
    }

    // Compute the declination angle of the sun
    private fun calculateSunDeclination(julianDate: Double): Double {
        return calculateSunPosition(julianDate)[0]
    }

    // Compute mid-day (Dhuhr or Zawal) time
    private fun calculateMidDay(offset: Double): Double {
        return try {
            val equationOfTime = calculateEquationOfTime(getJDate() + offset)
            normalizeHour(12.0 - equationOfTime)
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in calculateMidDay: $e", e)
            12.0 // Default mid-day time in case of error
        }
    }

    // Compute the time for a given angle G
    private fun calculateTimeForAngle(angle: Double, offset: Double): Double {
        return try {
            val declination = calculateSunDeclination(getJDate() + offset)
            val midDayTime = calculateMidDay(offset)
            val latitude = getLat()

            val numerator = -sinDegrees(angle) - sinDegrees(declination) * sinDegrees(latitude)
            val denominator = cosDegrees(declination) * cosDegrees(latitude)
            val hourAngle = arccosDegrees(numerator / denominator) / 15.0

            midDayTime + if (angle > 90.0) -hourAngle else hourAngle
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in calculateTimeForAngle: $e", e)
            0.0 // Default time in case of error
        }
    }

    // Compute the time of Asr (Shafii: step=1, Hanafi: step=2)
    private fun calculateAsrTime(asrStep: Double, offset: Double): Double {
        return try {
            val declination = calculateSunDeclination(getJDate() + offset)
            val latitude = getLat()
            val angle = -arccotDegrees(asrStep + tanDegrees(abs(latitude - declination)))
            calculateTimeForAngle(angle, offset)
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in calculateAsrTime: $e", e)
            0.0 // Default time in case of error
        }
    }


    // ---------------------- Misc Functions -----------------------
    // compute the difference between two times
    private fun calculateTimeDifference(time1: Double, time2: Double): Double {
        return normalizeHour(time2 - time1)
    }

    // -------------------- Interface Functions --------------------
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // set the angle for calculating Fajr
    private fun setFajrAngle(angle: Double) {
        try {
            val params = doubleArrayOf(angle, -1.0, -1.0, -1.0, -1.0)
            setCustomParams(params)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // set the angle for calculating Maghrib
    private fun setMaghribAngle(angle: Double) {
        try {
            val params = doubleArrayOf(-1.0, 0.0, angle, -1.0, -1.0)
            setCustomParams(params)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // set the angle for calculating Isha
    private fun setIshaAngle(angle: Double) {
        try {
            val params = doubleArrayOf(-1.0, -1.0, -1.0, 0.0, angle)
            setCustomParams(params)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // set the minutes after Sunset for calculating Maghrib
    private fun setMaghribMinutes(minutes: Double) {
        try {
            val params = doubleArrayOf(-1.0, 1.0, minutes, -1.0, -1.0)
            setCustomParams(params)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // set the minutes after Maghrib for calculating Isha
    private fun setIshaMinutes(minutes: Double) {
        try {
            val params = doubleArrayOf(-1.0, -1.0, -1.0, 1.0, minutes)
            setCustomParams(params)
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    // Convert double hours to 24-hour format
    private fun convertTo24HourFormat(time: Double): String {
        return try {
            if (java.lang.Double.isNaN(time)) {
                return "Invalid Time"
            }

            var adjustedTime = normalizeHour(time + 0.5 / 60.0) // Add 0.5 minutes to round
            val hours = floor(adjustedTime).toInt()
            val minutes = floor((adjustedTime - hours) * 60.0).toInt()

            // Format the time with leading zeroes if needed
            String.format("%02d:%02d", hours, minutes)
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in convertTo24HourFormat: $e", e)
            "Invalid Time"
        }
    }

    // Convert double hours to 12-hour format with AM/PM
    private fun convertTo12HourFormat(time: Double, noSuffix: Boolean = false): String {
        return try {
            if (java.lang.Double.isNaN(time)) {
                return "Invalid Time"
            }

            var adjustedTime = normalizeHour(time + 0.5 / 60.0) // Add 0.5 minutes to round
            var hours = floor(adjustedTime).toInt()
            val minutes = floor((adjustedTime - hours) * 60).toInt()
            val suffix = if (hours >= 12) "pm" else "am"

            hours = (hours % 12).takeIf { it != 0 } ?: 12 // Convert hours to 12-hour format

            // Format the time with leading zeroes if needed
            val formattedTime = String.format("%02d:%02d", hours, minutes)

            if (noSuffix) formattedTime else "$formattedTime $suffix"
        } catch (e: Exception) {
            Log.e("PrayerTime", "Error in convertTo12HourFormat: $e", e)
            "Invalid Time"
        }
    }

    // Convert double hours to 12-hour format without AM/PM suffix
    private fun convertTo12HourFormatNoSuffix(time: Double): String {
        return convertTo12HourFormat(time, noSuffix = true)
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
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
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
        return times
    }

    // Tune timings for adjustments
    // Set time offsets
    private fun tune(offsetTimes: IntArray) {
        try {
//            for (int i = 0; i < offsetTimes.length; i++) { // offsetTimes length
//                // should be 7 in order
//                // of Fajr, Sunrise,
//                // Dhuhr, Asr, Sunset,
//                // Maghrib, Isha
//                this.offsets[i] = offsetTimes[i];
//            }
            for (i in offsetTimes.indices) {
                // offsetTimes length
                // should be 7 in order
                // of Fajr, Sunrise,
                // Dhuhr, Asr, Sunset,
                // Maghrib, Isha
                offsets!![i] = offsetTimes[i]
            }
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
    }

    private fun tuneTimes(times: DoubleArray): DoubleArray {
        try {
            for (i in times.indices) {
                times[i] = times[i] + offsets!![i] / 60.0
            }
        } catch (e: Exception) {
            Log.i("PrayerTime", "main: Inside Exception = $e")
            e.printStackTrace()
        }
        return times
    }

    fun clearPrayerResources() {
        if (timeNames != null && timeNames?.isNotEmpty() == true) {
            timeNames?.clear()
            timeNames = null
        }
        if (prayerTimeList != null && prayerTimeList?.isNotEmpty() == true) {
            prayerTimeList?.clear()
            prayerTimeList = null
        }
        if (methodParams != null && methodParams?.isNotEmpty() == true) {
            methodParams?.clear()
            methodParams = null
        }
    }

    private fun refreshPrayerTimes(mainSharedPref: SharedPrefManager) {

        try {

            val defaultTz = TimeZone.getDefault()

            //Get NY calendar object with current date/time
            val defaultCalc = Calendar.getInstance(defaultTz)

            //Get offset from UTC, accounting for DST
            val defaultTzOffsetMs =
                defaultCalc[Calendar.ZONE_OFFSET] + defaultCalc[Calendar.DST_OFFSET]
            val timezone = defaultTzOffsetMs.toDouble() / (1000 * 60 * 60)

            setTimeFormat(mainSharedPref.prayerTimeFormat)
            setCalcMethod(mainSharedPref.prayerCalMethod)
            setAsrJuristic(mainSharedPref.asrJuristic)
            setAdjustHighLats(mainSharedPref.prayerHighLats)

            //            int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
//            tune(offsets);
            val now = Date()
            val cal = Calendar.getInstance()
            cal.time = now

//            SimpleDateFormat timeFormatter;
            var timeFormat = ""

            timeFormat = if (mainSharedPref.prayerTimeFormat == 1) {
                "hh:mm aa"
            } else {
                "hh:mm";
            }
            timeFormatter = SimpleDateFormat(timeFormat, Locale.US)

            val prayerTimes = getPrayerTimes(
                cal,
                java.lang.Double.longBitsToDouble(mainSharedPref.prayerLatitude),
                java.lang.Double.longBitsToDouble(mainSharedPref.prayerLongitude), timezone
            )
            val prayerNames = getTimeNames()
            for (i in prayerTimes.indices) {
                if (isActive) {
                    val prayerTimeModel = PrayerTimeModel()
                    prayerTimeModel.prayerName = prayerNames!![i]

                    val indexNamaz = timeFormatter!!.parse(prayerTimes[i]!!)
                    var updateTime = prayerTimes[i]

                    if (i != 1 && i != 4) {
                        var firstID = 1
                        var nextNamazTime: String? = ""
                        if (i == 0) {
                            if (indexNamaz != null) {
                                indexNamaz.minutes = indexNamaz.minutes
                                updateTime = timeFormatter!!.format(indexNamaz)
                                prayerTimeModel.prayerTime = updateTime
                            }
                            val indexNext = timeFormatter!!.parse(prayerTimes[2]!!)
                            if (indexNext != null) {
                                indexNext.minutes = indexNext.minutes
                                nextNamazTime = timeFormatter!!.format(indexNext)
                            }
                        } else if (i == 2) {
                            if (indexNamaz != null) {
                                indexNamaz.minutes = indexNamaz.minutes
                                updateTime = timeFormatter!!.format(indexNamaz)
                                prayerTimeModel.prayerTime = updateTime
                            }
                            firstID = 2
                            val indexNext = timeFormatter!!.parse(prayerTimes[3]!!)
                            if (indexNext != null) {
                                indexNext.minutes = indexNext.minutes
                                nextNamazTime = timeFormatter!!.format(indexNext)
                            }
                        } else if (i == 3) {
                            if (indexNamaz != null) {
                                indexNamaz.minutes = indexNamaz.minutes
                                updateTime = timeFormatter!!.format(indexNamaz)
                                prayerTimeModel.prayerTime = updateTime
                            }
                            firstID = 3
                            val indexNext = timeFormatter!!.parse(prayerTimes[5]!!)
                            if (indexNext != null) {
                                indexNext.minutes = indexNext.minutes
                                nextNamazTime = timeFormatter!!.format(indexNext)
                            }
                        } else if (i == 5) {
                            if (indexNamaz != null) {
                                indexNamaz.minutes = indexNamaz.minutes
                                updateTime = timeFormatter!!.format(indexNamaz)
                                prayerTimeModel.prayerTime = updateTime
                            }
                            firstID = 4
                            val indexNext = timeFormatter!!.parse(prayerTimes[6]!!)
                            if (indexNext != null) {
                                indexNext.minutes = indexNext.minutes
                                nextNamazTime = timeFormatter!!.format(indexNext)
                            }
                        } else if (i == 6) {
                            if (indexNamaz != null) {
                                indexNamaz.minutes = indexNamaz.minutes
                                updateTime = timeFormatter!!.format(indexNamaz)
                                prayerTimeModel.prayerTime = updateTime
                            }
                            firstID = 5
                            val indexNext = timeFormatter!!.parse(prayerTimes[0]!!)
                            if (indexNext != null) {
                                indexNext.minutes = indexNext.minutes
                                nextNamazTime = timeFormatter!!.format(indexNext)
                            }

                            val nowFajr = Date()
                            val calFajr = Calendar.getInstance()
                            calFajr.time = nowFajr
                            calFajr.add(Calendar.DATE, 1)
                            val prayerTimeForFajr = getPrayerTimes(
                                calFajr,
                                java.lang.Double.longBitsToDouble(mainSharedPref.prayerLatitude),
                                java.lang.Double.longBitsToDouble(mainSharedPref.prayerLongitude),
                                timezone
                            )
                            if (prayerTimeForFajr.size > 0) {
                                val indexNextFajr = timeFormatter!!.parse(prayerTimeForFajr[0]!!)
                                if (indexNextFajr != null) {
                                    indexNextFajr.minutes = indexNextFajr.minutes
                                    nextDayFajrTime = timeFormatter!!.format(indexNextFajr)
                                }
                            }
                        }

                    }
                    if (i == 1) {
                        if (indexNamaz != null) {
                            updateTime = timeFormatter!!.format(indexNamaz)
                            prayerTimeModel.prayerTime = updateTime
                        }

                    } else if (i == 4) {
                        if (indexNamaz != null) {
                            indexNamaz.minutes = indexNamaz.minutes
                            updateTime = timeFormatter!!.format(indexNamaz)
                            prayerTimeModel.prayerTime = updateTime
                        }

                    }
                    prayerTimeList!!.add(prayerTimeModel)
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            Log.i("prayersData", "main: Inside Exception = $e")
            e.printStackTrace()
        }

    }

    fun getCalcMethod(): Int {
        return calcMethod
    }

    fun setCalcMethod(calcMethod: Int) {
        this.calcMethod = calcMethod
    }

    fun getAsrJuristic(): Int {
        return asrJuristic
    }

    fun setAsrJuristic(asrJuristic: Int) {
        this.asrJuristic = asrJuristic
    }

    fun getDhuhrMinutes(): Int {
        return dhuhrMinutes
    }

    fun setDhuhrMinutes(dhuhrMinutes: Int) {
        this.dhuhrMinutes = dhuhrMinutes
    }

    fun getAdjustHighLats(): Int {
        return adjustHighLats
    }

    fun setAdjustHighLats(adjustHighLats: Int) {
        this.adjustHighLats = adjustHighLats
    }

    fun getTimeFormat(): Int {
        return timeFormat
    }

    fun setTimeFormat(timeFormat: Int) {
        this.timeFormat = timeFormat
    }

    fun getLat(): Double {
        return lat
    }

    fun setLat(lat: Double) {
        this.lat = lat
    }

    fun getLng(): Double {
        return lng
    }

    fun setLng(lng: Double) {
        this.lng = lng
    }

    fun getTimeZone(): Double {
        return timeZone
    }

    fun setTimeZone(timeZone: Double) {
        this.timeZone = timeZone
    }

    fun getJDate(): Double {
        return JDate
    }

    fun setJDate(jDate: Double) {
        JDate = jDate
    }

    private fun getNumIterations(): Int {
        return numIterations
    }

    private fun setNumIterations(numIterations: Int) {
        this.numIterations = numIterations
    }

    fun getTimeNames(): ArrayList<String>? {
        return timeNames
    }

}