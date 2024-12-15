package com.orbitalsonic.opt.utils

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

// Normalize an angle in degrees to the range [0, 360)
internal fun normalizeAngle(angle: Double): Double {
    val normalized = angle % 360.0
    return if (normalized < 0) normalized + 360.0 else normalized
}

// Normalize hours to the range [0, 24)
internal fun normalizeHour(hour: Double): Double {
    val normalized = hour % 24.0
    return if (normalized < 0) normalized + 24.0 else normalized
}

// Convert radians to degrees
internal fun radiansToDegrees(radians: Double): Double {
    return ((radians * 180.0) / Math.PI)
}

// Convert degrees to radians
internal fun degreesToRadians(degrees: Double): Double {
    return ((degrees * Math.PI) / 180.0)
}

// Sine of an angle in degrees
internal fun sinDegrees(degrees: Double): Double {
    return sin(degreesToRadians(degrees))
}

// Cosine of an angle in degrees
internal fun cosDegrees(degrees: Double): Double {
    return cos(degreesToRadians(degrees))
}

// Tangent of an angle in degrees
internal fun tanDegrees(degrees: Double): Double {
    return tan(degreesToRadians(degrees))
}

// Arcsine in degrees
internal fun arcsinDegrees(value: Double): Double {
    return radiansToDegrees(asin(value))
}

// Arccosine in degrees
internal fun arccosDegrees(value: Double): Double {
    return radiansToDegrees(acos(value))
}

// Arctangent in degrees
internal fun arctanDegrees(value: Double): Double {
    return radiansToDegrees(atan(value))
}

// Arctangent2 (two-argument arctangent) in degrees
internal fun arctan2Degrees(y: Double, x: Double): Double {
    return radiansToDegrees(atan2(y, x))
}

// Arccotangent in degrees
internal fun arccotDegrees(value: Double): Double {
    return radiansToDegrees(atan2(1.0, value))
}