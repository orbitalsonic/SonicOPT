package com.orbitalsonic.offlineprayertime.utils

import android.util.Log
import kotlin.math.abs

// Compute declination angle of the sun and equation of time
 fun calculateSunPosition(julianDate: Double): DoubleArray {
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
 fun calculateEquationOfTime(julianDate: Double): Double {
    return calculateSunPosition(julianDate)[1]
}

// Compute the declination angle of the sun
 fun calculateSunDeclination(julianDate: Double): Double {
    return calculateSunPosition(julianDate)[0]
}

// Compute mid-day (Dhuhr or Zawal) time
fun calculateMidDay(offset: Double,jDate: Double): Double {
    return try {
        val equationOfTime = calculateEquationOfTime(jDate + offset)
        normalizeHour(12.0 - equationOfTime)
    } catch (e: Exception) {
        Log.e("PrayerTime", "Error in calculateMidDay: $e", e)
        12.0 // Default mid-day time in case of error
    }
}

// Compute the time for a given angle G
 fun calculateTimeForAngle(angle: Double, offset: Double,latitude: Double,jDate: Double): Double {
    return try {
        val declination = calculateSunDeclination(jDate + offset)
        val midDayTime = calculateMidDay(offset,jDate)

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
 fun calculateAsrTime(asrStep: Double, offset: Double,latitude: Double,jDate: Double): Double {
    return try {
        val declination = calculateSunDeclination(jDate + offset)
        val angle = -arccotDegrees(asrStep + tanDegrees(abs(latitude - declination)))
        calculateTimeForAngle(angle, offset,latitude,jDate)
    } catch (e: Exception) {
        Log.e("PrayerTime", "Error in calculateAsrTime: $e", e)
        0.0 // Default time in case of error
    }
}