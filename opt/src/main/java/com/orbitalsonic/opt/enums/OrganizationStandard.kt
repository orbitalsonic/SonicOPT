package com.orbitalsonic.opt.enums

/**
 * Represents different methods of calculating prayer times.
 * These methods are based on different Islamic organizations and conventions.
 */
enum class OrganizationStandard {

    /**
     * MAKKAH: Umm al-Qura method used in Saudi Arabia.
     * Fajr angle is 18.5° and Isha is a fixed interval of 90 minutes.
     */
    MAKKAH,

    /**
     * EGYPT: Egyptian General Authority of Survey.
     * Fajr angle is 19.5° and Isha angle is 17.5°.
     */
    EGYPT,

    /**
     * TEHRAN: Institute of Geophysics, University of Tehran.
     * Fajr angle is 17.7° and Isha angle is 14°.
     */
    TEHRAN,

    /**
     * JAFARI: Ithna Ashari method.
     * Fajr angle is 16° and Isha angle is 14°.
     */
    JAFARI,

    /**
     * KARACHI: University of Islamic Sciences, Karachi.
     * Fajr and Isha angles are both 18°.
     */
    KARACHI,

    /**
     * ISNA: Islamic Society of North America.
     * Fajr and Isha angles are both 15°.
     */
    ISNA,

    /**
     * MWL: Muslim World League.
     * Fajr angle is 18° and Isha angle is 17°.
     */
    MWL,

    /**
     * CUSTOM: User-defined custom settings for angles or intervals.
     */
    CUSTOM
}
