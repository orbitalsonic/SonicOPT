package com.orbitalsonic.offlineprayertime.enums

/**
 * Represents different methods of calculating prayer times.
 * These methods are based on different Islamic organizations and conventions.
 */
enum class OrganizationStandard {

    /**
     * STANDARD: Commonly used in the Middle East and many Islamic countries.
     * Assumes a Fajr angle of 18° and Isha angle of 18°.
     */
    STANDARD,

    /**
     * UMM_AL_QURA: Used primarily in Saudi Arabia, specifically Makkah.
     * Assumes a Fajr angle of 18.5° and a fixed interval for Isha (90 minutes after Maghrib).
     */
    UMM_AL_QURA,

    /**
     * EGYPTIAN: Often used in Egypt and nearby regions.
     * Assumes a Fajr angle of 19.5° and Isha angle of 17.5°.
     */
    EGYPTIAN,

    /**
     * KARACHI: Used in Pakistan and nearby regions.
     * Assumes a Fajr angle of 18° and Isha angle of 18°.
     */
    KARACHI,

    /**
     * ISNA: Used in North America by the Islamic Society of North America.
     * Assumes a Fajr angle of 15° and Isha angle of 15°.
     */
    ISNA
}
