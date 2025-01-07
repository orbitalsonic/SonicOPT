package com.orbitalsonic.sonicopt.enums

/**
 * Represents methods for adjusting prayer times at high latitudes
 * where the sun does not rise or set clearly during certain periods.
 */
enum class HighLatitudeAdjustment {
    /**
     * No Adjustment: No adjustment applied for high latitudes.
     */
    NO_ADJUSTMENT,

    /**
     * Middle of the Night: Divides the night into two equal halves for
     * estimating Fajr and Isha times.
     */
    MID_NIGHT,

    /**
     * Seventh of the Night: Divides the night into seven parts, with
     * Fajr at one-seventh and Isha at six-sevenths of the night.
     */
    ONE_SEVENTH,

    /**
     * Twilight Angle: Uses the angle of the sun below the horizon
     * to define Fajr and Isha times.
     */
    TWILIGHT_ANGLE
}

/**
--- High Latitude Adjustment ---

  1. No Adjustment
  2. Middle of The Night
  3. Seventh of The Night
  4. Twilight Angle
 */