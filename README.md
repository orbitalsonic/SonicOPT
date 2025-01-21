[![](https://jitpack.io/v/orbitalsonic/SonicOPT.svg)](https://jitpack.io/#orbitalsonic/SonicOPT)


> **⚠️ WARNING:** This library is currently under testing. Use it with caution as it may contain bugs or calculation error

# SonicOPT (Offline Prayer Time)

SonicOPT is a robust offline library for calculating precise Islamic prayer and fasting times. It offers daily, monthly, and yearly calculations with support for multiple conventions, high-latitude adjustments, Asr time methods, and customizable time formats, making it an essential tool for accurate scheduling.

---

## Features
- **Offline Functionality**: No internet connection is required.
- **Daily, Monthly, and Yearly Prayer Times**: Calculate prayer times for any duration.
- **Specific Date or Date Range Prayer Times**: Fetch precise prayer times for a specific date or range of dates.
- **Daily, Monthly, and Yearly Fasting Times**: Calculate fasting times for any duration.
- **Specific Date or Date Range Fasting Time:**: Fetch precise fasting times for a specific date or range of dates.
- **Multiple Time Formats**: Choose between 12-hour, 24-hour, or floating-point representations.
- **Manual Time Adjustments**: Allow precise manual corrections to calculated times.
- **Custom Prayer Angles**: Specify custom angles for prayer time calculations.
- **Asr Time Calculation**: Supports both Hanafi and Shafi methods for Asr time calculation.
- **High Latitude Adjustment**: Offers flexible adjustment methods, such as No Adjustment, Middle of the Night, Seventh of the Night, and Twilight Angle..
- **Prayer Time Conventions**: Supports multiple conventions including Muslim World League, Egyptian General Authority of Survey, Umm Al-Qura University, and more. Custom angles are also supported.

---

## Setup

### Step 1: Add Maven Repository
Add the following to your project-level build script (`build.gradle` or `settings.gradle`) for **Groovy** or **Kotlin DSL**:

#### Groovy DSL
```groovy
repositories {
   google()
   mavenCentral()
   maven { url "https://jitpack.io" }
}
```

#### Kotlin DSL
```kotlin
repositories {
    google()
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}
```

### Step 2: Add Dependency
Include the OPT library in your app-level build script (`build.gradle` or `build.gradle.kts`). Replace `x.x.x` with the latest version: [![](https://jitpack.io/v/orbitalsonic/SonicOPT.svg)](https://jitpack.io/#orbitalsonic/SonicOPT)


#### Groovy DSL
```groovy
dependencies {
    implementation 'com.github.orbitalsonic:SonicOPT:x.x.x'
}
```

#### Kotlin DSL
```kotlin
dependencies {
    implementation("com.github.orbitalsonic:SonicOPT:x.x.x")
}
```

### Step 3: Sync Gradle
Sync your Gradle project to fetch the dependency.

---

## Usage

### Prayer Time Manager
The `PrayerTimeManager` class is designed to fetch and calculate prayer and fasting times efficiently. To use the `PrayerTimeManager` class, begin by initializing it:

```kotlin
val prayerTimeManager = PrayerTimeManager()
```
---

### Fetching Prayer Times

Prayer times are calculated based on the provided latitude, longitude, high latitude adjustment, Asr juristic method, prayer time convention, and time format.

- **Manual Corrections**: You can adjust prayer times manually within a range of -59 to +59 minutes. If the adjustment exceeds this range, it defaults to 0 (no correction).
- **Custom Angles**: When the `PrayerTimeConvention` is set to CUSTOM, you can define specific angles for Fajr and Isha prayers. The default custom angles are:
    - **Fajr**: 9.0°
    - **Isha**: 14.0°

By utilizing these features, you can calculate accurate and tailored prayer times for any location and customize them to meet your specific needs.


#### Fetch Today Prayer Times
```kotlin
prayerTimeManager.fetchTodayPrayerTimes(
    latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
    longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
    highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
    asrJuristicMethod = AsrJuristicMethod.HANAFI,
    prayerTimeConvention = PrayerTimeConvention.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0, zuhrMinute = 0, asrMinute = 0, maghribMinute = 0, ishaMinute = 2),
    prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
    result.onSuccess { prayerItem ->
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(Date(prayerItem.date))
        Log.d("PrayerTimeTag", "Date: $formattedDate")
        prayerItem.prayerList.forEach {
            Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
        }
    }.onFailure { exception ->
        Log.e("PrayerTimeTag", "Error fetching daily prayer times", exception)
    }
}
```

#### Fetch Current Month Prayer Times
```kotlin
prayerTimeManager.fetchCurrentMonthPrayerTimes(
    latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
    longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
    highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
    asrJuristicMethod = AsrJuristicMethod.HANAFI,
    prayerTimeConvention = PrayerTimeConvention.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0, zuhrMinute = 0, asrMinute = 0, maghribMinute = 0, ishaMinute = 2),
    prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
    result.onSuccess { prayerItems ->
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        prayerItems.forEachIndexed { index, prayerItem ->
            val formattedDate = dateFormatter.format(Date(prayerItem.date))
            Log.d("PrayerTimeTag", "Day ${index + 1} (${formattedDate}):")
            prayerItem.prayerList.forEach {
                Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
            }
        }
    }.onFailure { exception ->
        Log.e("PrayerTimeTag", "Error fetching monthly prayer times", exception)
    }
}
```

#### Fetch Current Year Prayer Times
```kotlin
prayerTimeManager.fetchCurrentYearPrayerTimes(
    latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
    longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
    highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
    asrJuristicMethod = AsrJuristicMethod.HANAFI,
    prayerTimeConvention = PrayerTimeConvention.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0, zuhrMinute = 0, asrMinute = 0, maghribMinute = 0, ishaMinute = 2),
    prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
    result.onSuccess { prayerItems ->
      val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        prayerItems.forEachIndexed { monthIndex, monthlyPrayerItems ->
            Log.d("PrayerTimeTag", "Month ${monthIndex + 1}:")
            monthlyPrayerItems.forEachIndexed { dayIndex, prayerItem ->
                val formattedDate = dateFormatter.format(Date(prayerItem.date))
                Log.d("PrayerTimeTag", "Day ${dayIndex + 1} (${formattedDate}):")
                prayerItem.prayerList.forEach {
                    Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
                }
            }
        }
    }.onFailure { exception ->
        Log.e("PrayerTimeTag", "Error fetching yearly prayer times", exception)
    }
}
```

#### Fetch Specific Date Prayer Time
```kotlin

// Create a Calendar instance and set it to June 5, 2025
val calendar = GregorianCalendar(2025, Calendar.JUNE, 5)
// Convert the Calendar instance to a Date object
val date = calendar.time

prayerTimeManager.fetchSpecificDatePrayerTimes(
  latitude = 33.4979105,
  longitude = 73.0722461,
  date = date,
  highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
  asrJuristicMethod = AsrJuristicMethod.HANAFI,
  prayerTimeConvention = PrayerTimeConvention.KARACHI,
  timeFormat = TimeFormat.HOUR_12,
  prayerManualCorrection = PrayerManualCorrection(),
  prayerCustomAngle = PrayerCustomAngle()
) { result ->
  result.onSuccess { prayerItem ->
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(prayerItem.date))
    Log.d("PrayerTimeTag", "Date: $formattedDate")
    prayerItem.prayerList.forEach {
      Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
    }
  }.onFailure { exception ->
    Log.e("PrayerTimeTag", "Error fetching prayer times", exception)
  }
}

```

#### Fetch Date Range Prayer Time
```kotlin

// Create a Calendar instance and set it to June 5, 2025
val startCalendar = GregorianCalendar(2025, Calendar.JUNE, 25)
// Convert the Calendar instance to a Date object
val startDate = startCalendar.time

// Create a Calendar instance and set it to July 7, 2025
val endCalendar = GregorianCalendar(2025, Calendar.JULY, 7)
// Convert the Calendar instance to a Date object
val endDate = endCalendar.time

prayerTimeManager.fetchPrayerTimesForDateRange(
  latitude = 33.4979105,
  longitude = 73.0722461,
  startDate = startDate,
  endDate = endDate,
  highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
  asrJuristicMethod = AsrJuristicMethod.HANAFI,
  prayerTimeConvention = PrayerTimeConvention.KARACHI,
  timeFormat = TimeFormat.HOUR_12,
  prayerManualCorrection = PrayerManualCorrection(),
  prayerCustomAngle = PrayerCustomAngle()
) { result ->
  result.onSuccess { prayerItems ->
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    prayerItems.forEach { prayerItem ->
      val formattedDate = dateFormatter.format(Date(prayerItem.date))
      Log.d("PrayerTimeTag", "Date: $formattedDate")
      prayerItem.prayerList.forEach {
        Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
      }
    }
  }.onFailure { exception ->
    Log.e("PrayerTimeTag", "Error fetching prayer times", exception)
  }
}


```

---

## Fetching Fasting Times

Fasting times are calculated based on the Fajr and Maghrib prayer times. Sehri ends at the start time of the Fajr prayer, and Iftar begins at the start time of the Maghrib prayer. To calculate these times, provide the latitude, longitude, high latitude adjustment, and prayer time convention.

- **Fasting Times**:
    - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
    - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.

- **Manual Corrections**: Fasting times can be adjusted using manual corrections within a range of -59 to +59 minutes. If the adjustment exceeds this range, it defaults to 0 (no correction).

- **Custom Angles**: When `PrayerTimeConvention` is set to CUSTOM, specific angles for Fajr and Isha prayers can be defined. The default custom angles are:
    - **Fajr**: 9.0°
    - **Isha**: 14.0°

#### Fetch Today Fasting Times
```kotlin
prayerTimeManager.fetchTodayFastingTimes(
    latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
    longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
    highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
    prayerTimeConvention = PrayerTimeConvention.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0,maghribMinute = 0),
    prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
    result.onSuccess { fastingItem ->
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(Date(fastingItem.date))
        Log.d("FastingTimeTag", "Date: $formattedDate")
        Log.d("FastingTimeTag", "Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching daily fasting times", exception)
    }
}
```

#### Fetch Current Month Fasting Times
```kotlin
prayerTimeManager.fetchCurrentMonthFastingTimes(
    latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
    longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
    highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
    prayerTimeConvention = PrayerTimeConvention.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0,maghribMinute = 0),
    prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
    result.onSuccess { fastingTimes ->
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        fastingTimes.forEachIndexed { index, fastingItem ->
            val formattedDate = dateFormatter.format(Date(fastingItem.date))
            Log.d("FastingTimeTag", "Day ${index + 1} (${formattedDate}): Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
        }
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching monthly fasting times", exception)
    }
}
```

#### Fetch Current Year Fasting Times
```kotlin
prayerTimeManager.fetchCurrentYearFastingTimes(
   latitude = 33.4979105, // Example latitude (Islamabad, Pakistan)
   longitude = 73.0722461, // Example longitude (Islamabad, Pakistan)
   highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
   prayerTimeConvention = PrayerTimeConvention.KARACHI,
   timeFormat = TimeFormat.HOUR_12,
   prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0,maghribMinute = 0),
   prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
) { result ->
   result.onSuccess { fastingTimes ->
      val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
      fastingTimes.forEachIndexed { monthIndex, monthlyFastingList ->
         Log.d("FastingTimeTag", "Month ${monthIndex + 1}:")
         monthlyFastingList.forEachIndexed { dayIndex, fastingItem ->
            val formattedDate = dateFormatter.format(Date(fastingItem.date))
            Log.d("FastingTimeTag", "Day ${dayIndex + 1} (${formattedDate}): Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
         }
      }
   }.onFailure { exception ->
      Log.e("FastingTimeTag", "Error fetching yearly fasting times", exception)
   }
}
```
#### Fetch Specific Date Fasting Time
```kotlin

// Create a Calendar instance and set it to June 5, 2025
val calendar = GregorianCalendar(2025, Calendar.JUNE, 5)
// Convert the Calendar instance to a Date object
val date = calendar.time

prayerTimeManager.fetchSpecificDateFastingTimes(
  latitude = 33.4979105,
  longitude = 73.0722461,
  date = date,
  highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
  prayerTimeConvention = PrayerTimeConvention.KARACHI,
  timeFormat = TimeFormat.HOUR_12,
  prayerManualCorrection = PrayerManualCorrection(),
  prayerCustomAngle = PrayerCustomAngle()
) { result ->
  result.onSuccess { fastingItem ->
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(fastingItem.date))
    Log.d("FastingTimeTag", "Date: $formattedDate")
    Log.d("FastingTimeTag", "Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
  }.onFailure { exception ->
    Log.e("FastingTimeTag", "Error fetching fasting times", exception)
  }
}

```

#### Fetch Date Range Fasting Time
```kotlin

// Create a Calendar instance and set it to June 5, 2025
val startCalendar = GregorianCalendar(2025, Calendar.JUNE, 25)
// Convert the Calendar instance to a Date object
val startDate = startCalendar.time

// Create a Calendar instance and set it to July 7, 2025
val endCalendar = GregorianCalendar(2025, Calendar.JULY, 7)
// Convert the Calendar instance to a Date object
val endDate = endCalendar.time

prayerTimeManager.fetchFastingTimesForDateRange(
  latitude = 33.4979105,
  longitude = 73.0722461,
  startDate = startDate,
  endDate = endDate,
  highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
  prayerTimeConvention = PrayerTimeConvention.KARACHI,
  timeFormat = TimeFormat.HOUR_12,
  prayerManualCorrection = PrayerManualCorrection(),
  prayerCustomAngle = PrayerCustomAngle()
) { result ->
  result.onSuccess { fastingItems ->
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    fastingItems.forEach { fastingItem ->
      val formattedDate = dateFormatter.format(Date(fastingItem.date))
      Log.d("FastingTimeTag", "Date: $formattedDate")
      Log.d("FastingTimeTag", "Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
    }
  }.onFailure { exception ->
    Log.e("FastingTimeTag", "Error fetching fasting times", exception)
  }
}

```

---

### Models

#### PrayerItem
```kotlin
/**
 * Represents the prayer times for a specific day.
 *
 * @property date Date of the prayer times in milliseconds.
 * @property prayerList List of individual prayers and their times.
 */
data class PrayerItem(
    var date: Long,
    var prayerList: List<PrayerTimes>
)
```

#### PrayerTimes
```kotlin
/**
 * Represents an individual prayer.
 *
 * @property prayerName Name of the prayer (e.g., Fajr, Dhuhr, etc.).
 * @property prayerTime Formatted time of the prayer.
 * @property isCurrentPrayer Indicates if this is the currently active prayer.
 */
data class PrayerTimes(
    var prayerName: String,
    var prayerTime: String,
    var isCurrentPrayer: Boolean = false
)
```

#### PrayerManualCorrection
```kotlin
/**
 * Represents manual corrections to prayer times.
 * 
 * Each correction is specified in minutes and applied to adjust prayer times.
 * Corrections are restricted to a range of -59 to +59 minutes.
 * If a correction exceeds this range, it is reset to 0 (no correction).
 *
 * @property fajrMinute Manual adjustment for Fajr prayer.
 * @property zuhrMinute Manual adjustment for Zuhr prayer.
 * @property asrMinute Manual adjustment for Asr prayer.
 * @property maghribMinute Manual adjustment for Maghrib prayer.
 * @property ishaMinute Manual adjustment for Isha prayer.
 */
data class PrayerManualCorrection(
    var fajrMinute: Int = 0,
    var zuhrMinute: Int = 0,
    var asrMinute: Int = 0,
    var maghribMinute: Int = 0,
    var ishaMinute: Int = 0
)
```

#### PrayerCustomAngle
```kotlin
/**
 * Represents custom angles for Fajr and Isha prayers.
 * 
 * These angles are used when the prayer time convention is set to CUSTOM.
 *
 * @property fajrAngle The custom angle for calculating Fajr prayer time.
 * @property ishaAngle The custom angle for calculating Isha prayer time.
 */
data class PrayerCustomAngle(
    val fajrAngle: Double = 9.0,
    val ishaAngle: Double = 14.0
)
```

#### FastingItem
```kotlin
/**
 * Represents fasting times for a specific day.
 *
 * @property date Date of the fasting times in milliseconds.
 * @property sehriTime Time for Sehri (pre-dawn meal).
 * @property iftaarTime Time for Iftar (breaking the fast).
 */
data class FastingItem(
    var date: Long,
    val sehriTime: String,
    val iftaarTime: String
)
```
---


## Configuration Options

### High Latitude Adjustment (`HighLatitudeAdjustment`)

| **Option**       | **Description**                                                                                 |
|------------------|-------------------------------------------------------------------------------------------------|
| `NO_ADJUSTMENT`  | No adjustment applied for high latitudes.                                                       |
| `TWILIGHT_ANGLE` | Uses the angle of the sun below the horizon to define Fajr and Isha times.                      |
| `MID_NIGHT`      | Divides the night into two equal halves for estimating Fajr and Isha times.                     |
| `ONE_SEVENTH`    | Divides the night into seven parts, with Fajr at one-seventh and Isha at six-sevenths.          |

### Asr Time Calculation (`AsrJuristicMethod`)

| **Option**      | **Description**                                                                                 |
|-----------------|-------------------------------------------------------------------------------------------------|
| `SHAFI`         | Asr begins when the shadow of an object equals its height (Shafi'i school of thought).          |
| `HANAFI`        | Asr begins when the shadow of an object is twice its height (Hanafi school of thought).         |

### Prayer Time Convention (`PrayerTimeConvention`)

| **Option**               | **Description**                                                               |
|--------------------------|-------------------------------------------------------------------------------|
| `MWL`                    | Muslim World League (Fajr: 18.0°, Isha: 17.0°).                               |
| `EGYPT`                  | Egyptian General Authority of Survey (Fajr: 19.5°, Isha: 17.5°).              |
| `KARACHI`                | University of Islamic Sciences, Karachi (Fajr 18.0°, Isha: 18.0°).            |
| `MAKKAH`                 | Umm Al-Qura University, Makkah (Fajr: 18.5°, Isha: 90 minutes after Maghrib). |
| `DUBAI`                  | Dubai (Fajr: 18.2°, Isha: 18.2°).                                             |
| `MOONSIGHTING_COMMITTEE` | Moonsighting Committee (Fajr: 18.0°, Isha: 18.0°).                            |
| `ISNA`                   | Islamic Society of North America (Fajr: 15.0°, Isha: 15.0°).                  |
| `KUWAIT`                 | Kuwait (Fajr: 18.0°, Isha: 17.5°).                                            |
| `QATAR`                  | Qatar (Fajr: 18.0°, Isha: 90 minutes after Maghrib).                          |
| `SINGAPORE`              | Singapore (Fajr: 20.0°, Isha: 18.0°).                                         |
| `TEHRAN`                 | Tehran (Fajr: 17.7°, Isha: 14.0°).                                            |
| `JAFFARI`                | Jaffari (Fajr: 16.0°, Isha: 14,0°).                                           |
| `GULF_REGION`            | Gulf Region (Fajr: 19.5°, Isha: 90 minutes after Maghrib).                    |
| `FRANCE`                 | France (Fajr: 12.0°, Isha: 12.0°).                                            |
| `TURKEY`                 | Turkey (Fajr: 18.0°, Isha: 17.0°).                                            |
| `RUSSIA`                 | Russia (Fajr: 16.0°, Isha: 15.0°).                                            |
| `CUSTOM`                 | Custom angles (default Fajr: 9.0°, Isha: 14.0°).                              |

### Time Format (`TimeFormat`)

| **Option**     | **Description**                                                                                   |
|----------------|---------------------------------------------------------------------------------------------------|
| `HOUR_12`      | 12-hour format with AM/PM suffix (e.g., 5:00 AM).                                                 |
| `HOUR_12_NS`   | 12-hour format without AM/PM suffix (e.g., 5:00).                                                 |
| `HOUR_24`      | 24-hour format (e.g., 17:00).                                                                     |
| `FLOATING`     | Floating-point representation of hours (e.g., 5.5 for 5:30).                                      |

---

## Contributing
Contributions are welcome! Fork the repository, make changes, and submit a pull request.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

Copyright OrbitalSonic

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
