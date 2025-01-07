package com.orbitalsonic.sonicopt.enums

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Represents different methods of calculating prayer times.
 * These methods are based on various Islamic organizations and conventions.
 */
enum class PrayerTimeConvention {

    /**
     * Muslim World League (MWL):
     * Fajr angle is 18.0° and Isha angle is 17.0°.
     */
    MWL,

    /**
     * Egyptian General Authority of Survey:
     * Fajr angle is 19.5° and Isha angle is 17.5°.
     */
    EGYPT,

    /**
     * University of Islamic Sciences, Karachi:
     * Fajr angle is 18.0° and Isha angle is 18.0°.
     */
    KARACHI,

    /**
     * Umm Al-Qura University, Makkah:
     * Fajr angle is 18.5° and Isha is calculated as a fixed interval of 90 minutes after Maghrib.
     */
    MAKKAH,

    /**
     * Dubai:
     * Fajr angle is 18.2° and Isha angle is 18.2°.
     */
    DUBAI,

    /**
     * Moonsighting Committee:
     * Fajr angle is 18.0° and Isha angle is 18.0°.
     */
    MOONSIGHTING_COMMITTEE,

    /**
     * Islamic Society of North America (ISNA):
     * Fajr angle is 15.0° and Isha angle is 15.0°.
     */
    ISNA,

    /**
     * Kuwait:
     * Fajr angle is 18.0° and Isha angle is 17.5°.
     */
    KUWAIT,

    /**
     * Qatar:
     * Fajr angle is 18.0° and Isha is calculated as a fixed interval of 90 minutes after Maghrib.
     */
    QATAR,

    /**
     * Majlis Ugama Islam Singapura (Singapore):
     * Fajr angle is 20.0° and Isha angle is 18.0°.
     */
    SINGAPORE,

    /**
     * Institute of Geophysics, University of Tehran:
     * Fajr angle is 17.7° and Isha angle is 14.0°.
     */
    TEHRAN,

    /**
     * Jaffari (Ithna Ashari method):
     * Fajr angle is 16.0° and Isha angle is 14.0°.
     */
    JAFFARI,

    /**
     * The Gulf Region:
     * Fajr angle is 19.5° and Isha is calculated as a fixed interval of 90 minutes after Maghrib.
     */
    GULF_REGION,

    /**
     * Union Organization islamic de France:
     * Fajr angle is 12.0° and Isha angle is 12.0°.
     */
    FRANCE,

    /**
     * Presidency of Religious Affairs, Turkey:
     * Fajr angle is 18.0° and Isha angle is 17.0°.
     */
    TURKEY,

    /**
     * Spiritual Administration of Muslims of Russia:
     * Fajr angle is 16.0° and Isha angle is 15.0°.
     */
    RUSSIA,

    /**
     * Custom Angles:
     * User-defined settings for Fajr and Isha angles.
     * Default values: Fajr angle is 9.0° and Isha angle is 14.0°.
     */
    CUSTOM
}


/**

--- Prayer Time Conventions ---

1. Muslim World League
Fajr: 18.0°, Isha: 17.0°

2. Egyption General Authority of Survey
Fajr: 19.5°, Isha: 17.5°

3. University of Islamic Sciences, Karachi
Fajr: 18.0°, Isha: 18.0°

4. Umm Al-Qura University, Makkah
Fajr: 18.5°, Isha: 90 Minutes

5. Dubai
Fajr: 18.2°, Isha: 18.2°

6. Moonsighting Committee
Fajr: 18.0°, Isha: 18.0°

7. Islamic Society of North America (ISNA)
Fajr: 15.0°, Isha: 15.0°

8. Kuwait
Fajr: 18.0°, Isha: 17.5°

9. Qatar
Fajr: 18.0°, Isha: 90 minutes

10. Majlis Ugama Islam Singapura, Singapore
Fajr: 20.0°, Isha: 18.0°

11. Institute of Geophysics, University of Tehran
Fajr: 17.7°, Isha: 14.0°

12. Jaffari
Fajr: 16.0°, Isha: 14.0°

13. The Gulf Region
Fajr: 19.5°, Isha: 90 minutes

14. Union Organization islamic de France
Fajr: 12.0°, Isha: 12.0°

15. Presidency of Religious Affairs, Turkey
Fajr: 18.0°, Isha: 17.0°

16. Spiritual Administration of Muslims of Russia
Fajr: 16.0°, Isha: 15.0°

17. Custom Angles
Fajr: 9.0°, Isha: 14.0°

 */
