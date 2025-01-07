package com.orbitalsonic.sonicopt.utils

import android.util.Log
import java.util.Date
import java.util.TimeZone
import kotlin.math.floor

// Compute local time zone offset for the current date (in hours)
internal fun getLocalTimeZone(): Double {
    val timeZone = TimeZone.getDefault()
    return timeZone.rawOffset / 3600000.0 // Convert milliseconds to hours
}

// Compute the base time zone offset of the system (in hours)
internal fun getBaseTimeZone(): Double {
    val timeZone = TimeZone.getDefault()
    return timeZone.rawOffset / 3600000.0 // Convert milliseconds to hours
}

// Detect daylight saving time offset for the current date (in milliseconds)
internal fun getDaylightSavingOffset(): Double {
    val timeZone = TimeZone.getDefault()
    return timeZone.dstSavings.toDouble()
}

// Calculate Julian date from a calendar date
internal fun calculateJulianDate(year: Int, month: Int, day: Int): Double {
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
internal fun calculateJulianDateFromEpoch(year: Int, month: Int, day: Int): Double {
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

// Convert double hours to 24-hour format
internal fun convertTo24HourFormat(time: Double): String {
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
internal fun convertTo12HourFormat(time: Double, noSuffix: Boolean = false): String {
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
internal fun convertTo12HourFormatNoSuffix(time: Double): String {
    return convertTo12HourFormat(time, noSuffix = true)
}

// Convert time to floating-point representation (e.g., 5.5 for 5:30)
internal fun convertToFloatingFormat(time: Double): String {
    return try {
        if (java.lang.Double.isNaN(time)) {
            return "Invalid Time"
        }

        // Normalize the time and round to avoid floating-point precision issues
        val adjustedTime = normalizeHour(time + 0.5 / 60.0) // Add 0.5 minutes to round
        val hours = floor(adjustedTime)
        val minutes = (adjustedTime - hours) * 60.0

        // Calculate the floating-point representation
        val floatingTime = hours + minutes / 60.0

        // Format to 2 decimal places
        String.format("%.2f", floatingTime)
    } catch (e: Exception) {
        Log.e("PrayerTime", "Error in convertToFloatingFormat: $e", e)
        "Invalid Time"
    }
}


// compute the difference between two times
internal fun calculateTimeDifference(time1: Double, time2: Double): Double {
    return normalizeHour(time2 - time1)
}

