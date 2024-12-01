package com.orbitalsonic.offlineprayertime.enums

/**
 * Represents the two juristic methods for calculating Asr prayer time.
 * These methods define the shadow ratio for determining Asr.
 */
enum class JuristicMethod {

    /**
     * SHAFII: Used in the Shafi'i school of thought.
     * Asr begins when the shadow of an object equals its height.
     */
    SHAFII,

    /**
     * HANAFI: Used in the Hanafi school of thought.
     * Asr begins when the shadow of an object is twice its height.
     */
    HANAFI
}
