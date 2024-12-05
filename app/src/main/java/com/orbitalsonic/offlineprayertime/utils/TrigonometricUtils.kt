package com.orbitalsonic.offlineprayertime.utils

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

// Normalize an angle in degrees to the range [0, 360)
fun normalizeAngle(angle: Double): Double {
    val normalized = angle % 360.0
    return if (normalized < 0) normalized + 360.0 else normalized
}

// Normalize hours to the range [0, 24)
fun normalizeHour(hour: Double): Double {
    val normalized = hour % 24.0
    return if (normalized < 0) normalized + 24.0 else normalized
}

// Convert radians to degrees
fun radiansToDegrees(radians: Double): Double {
    return ((radians * 180.0) / Math.PI)
}

// Convert degrees to radians
fun degreesToRadians(degrees: Double): Double {
    return ((degrees * Math.PI) / 180.0)
}

// Sine of an angle in degrees
fun sinDegrees(degrees: Double): Double {
    return sin(degreesToRadians(degrees))
}

// Cosine of an angle in degrees
fun cosDegrees(degrees: Double): Double {
    return cos(degreesToRadians(degrees))
}

// Tangent of an angle in degrees
fun tanDegrees(degrees: Double): Double {
    return tan(degreesToRadians(degrees))
}

// Arcsine in degrees
fun arcsinDegrees(value: Double): Double {
    return radiansToDegrees(asin(value))
}

// Arccosine in degrees
fun arccosDegrees(value: Double): Double {
    return radiansToDegrees(acos(value))
}

// Arctangent in degrees
fun arctanDegrees(value: Double): Double {
    return radiansToDegrees(atan(value))
}

// Arctangent2 (two-argument arctangent) in degrees
fun arctan2Degrees(y: Double, x: Double): Double {
    return radiansToDegrees(atan2(y, x))
}

// Arccotangent in degrees
fun arccotDegrees(value: Double): Double {
    return radiansToDegrees(atan2(1.0, value))
}