package com.orbitalsonic.sonicopt.enums

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Represents the format for displaying time.
 */
enum class TimeFormat {
    /**
     * HOUR_12: 12-hour format with AM/PM suffix (e.g., 5:00 AM).
     */
    HOUR_12,

    /**
     * HOUR_12_NS: 12-hour format without AM/PM suffix (e.g., 5:00).
     */
    HOUR_12_NS,

    /**
     * HOUR_24: 24-hour format (e.g., 17:00).
     */
    HOUR_24,

    /**
     * FLOATING: Floating-point representation of hours (e.g., 5.5 for 5:30).
     */
    FLOATING
}
