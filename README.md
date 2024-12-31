> **⚠️ WARNING:** This library is currently under testing. Use it with caution as it may contain bugs or incomplete features.


[![](https://jitpack.io/v/orbitalsonic/opt.svg)](https://jitpack.io/#orbitalsonic/opt)

# OPT (Offline Prayer Time)

OPT is a powerful and offline library for calculating Islamic prayer and fasting times. It provides accurate prayer times for daily, monthly, and yearly durations based on location (latitude and longitude). The library supports multiple calculation methods, high-latitude adjustments, and customizable time formats, making it a versatile tool for developers.

---

## Features
- **Daily, Monthly, and Yearly Prayer Times**: Calculate prayer times for any duration.
- **Daily, Monthly, and Yearly Fasting Times**: Fetch fasting times based on prayer data.
- **Customizable Calculations**: Supports juristic methods and high-latitude adjustments.
- **Multiple Time Formats**: Choose between 12-hour, 24-hour, or floating-point representations.
- **Offline Functionality**: No internet connection is required after initialization.

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
Include the OPT library in your app-level build script (`build.gradle` or `build.gradle.kts`). Replace `x.x.x` with the latest version: [![](https://jitpack.io/v/orbitalsonic/opt.svg)](https://jitpack.io/#orbitalsonic/opt)

#### Groovy DSL
```groovy
dependencies {
    implementation 'com.github.orbitalsonic:opt:x.x.x'
}
```

#### Kotlin DSL
```kotlin
dependencies {
    implementation("com.github.orbitalsonic:opt:x.x.x")
}
```

### Step 3: Sync Gradle
Sync your Gradle project to fetch the dependency.

---

## Usage

### Prayer Time Manager
To fetch prayer times, use the `PrayerTimeManager` class. The following examples demonstrate its usage.

#### Fetching Prayer Times

##### Initialize `PrayerTimeManager`
```kotlin
val prayerTimeManager = PrayerTimeManager()
```

#### Fetch Today Prayer Times
```kotlin
prayerTimeManager.fetchTodayPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerItem ->
        val date = Date(prayerItem.date)
        Log.d("PrayerTimeTag", "Date: $date")
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
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerItems ->
        prayerItems.forEachIndexed { index, prayerItem ->
            val date = Date(prayerItem.date)
            Log.d("PrayerTimeTag", "Day ${index + 1} (${date}):")
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
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerItems ->
        prayerItems.forEachIndexed { monthIndex, monthlyPrayerItems ->
            Log.d("PrayerTimeTag", "Month ${monthIndex + 1}:")
            monthlyPrayerItems.forEachIndexed { dayIndex, prayerItem ->
                val date = Date(prayerItem.date)
                Log.d("PrayerTimeTag", "Day ${dayIndex + 1} (${date}):")
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

---

## Fasting Time Manager
To fetch fasting times, use the `PrayerTimeManager` class in conjunction with fasting-related methods. The following examples demonstrate its usage.

### Fetching Fasting Times

#### Fetch Today Fasting Times
```kotlin
prayerTimeManager.fetchTodayFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { fastingItem ->
        val date = Date(fastingItem.date)
        Log.d("FastingTimeTag", "Date: $date")
        Log.d("FastingTimeTag", "Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching daily fasting times", exception)
    }
}
```

#### Fetch Current Month Fasting Times
```kotlin
prayerTimeManager.fetchCurrentMonthFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { fastingTimes ->
        fastingTimes.forEachIndexed { index, fastingItem ->
            val date = Date(fastingItem.date)
            Log.d("FastingTimeTag", "Day ${index + 1} (${date}): Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
        }
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching monthly fasting times", exception)
    }
}
```

#### Fetch Current Year Fasting Times
```kotlin
prayerTimeManager.fetchCurrentYearFastingTimes(
   latitude = 40.7128, // Example latitude (New York)
   longitude = -74.0060, // Example longitude (New York)
   highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
   juristicMethod = JuristicMethod.HANAFI,
   organizationStandard = OrganizationStandard.KARACHI,
   timeFormat = TimeFormat.HOUR_12
) { result ->
   result.onSuccess { fastingTimes ->
      fastingTimes.forEachIndexed { monthIndex, monthlyFastingList ->
         Log.d("FastingTimeTag", "Month ${monthIndex + 1}:")
         monthlyFastingList.forEachIndexed { dayIndex, fastingItem ->
            val date = Date(fastingItem.date)
            Log.d("FastingTimeTag", "Day ${dayIndex + 1} (${date}): Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
         }
      }
   }.onFailure { exception ->
      Log.e("FastingTimeTag", "Error fetching yearly fasting times", exception)
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

| **Option**     | **Description**                                                                                  |
|----------------|--------------------------------------------------------------------------------------------------|
| `NONE`         | No adjustment applied for high latitudes.                                                      |
| `ANGLE_BASED`  | Uses the angle of the sun below the horizon to define Fajr and Isha times.                     |
| `MID_NIGHT`    | Divides the night into two equal halves for estimating Fajr and Isha times.                    |
| `ONE_SEVENTH`  | Divides the night into seven parts, with Fajr at one-seventh and Isha at six-sevenths.          |

### Juristic Method (`JuristicMethod`)

| **Option**     | **Description**                                                                                  |
|----------------|--------------------------------------------------------------------------------------------------|
| `SHAFII`       | Asr begins when the shadow of an object equals its height (Shafi'i school of thought).          |
| `HANAFI`       | Asr begins when the shadow of an object is twice its height (Hanafi school of thought).         |

### Organization Standard (`OrganizationStandard`)

| **Option**     | **Description**                                                                                  |
|----------------|--------------------------------------------------------------------------------------------------|
| `MAKKAH`       | Umm al-Qura method used in Saudi Arabia (Fajr: 18.5°, Isha: fixed 90 minutes).                  |
| `EGYPT`        | Egyptian General Authority of Survey (Fajr: 19.5°, Isha: 17.5°).                                |
| `TEHRAN`       | University of Tehran method (Fajr: 17.7°, Isha: 14°).                                           |
| `JAFARI`       | Ithna Ashari method (Fajr: 16°, Isha: 14°).                                                     |
| `KARACHI`      | University of Islamic Sciences, Karachi (Fajr/Isha: 18°).                                       |
| `ISNA`         | Islamic Society of North America (Fajr/Isha: 15°).                                             |
| `MWL`          | Muslim World League (Fajr: 18°, Isha: 17°).                                                    |
| `CUSTOM`       | User-defined custom settings for angles or intervals.                                           |

### Time Format (`TimeFormat`)

| **Option**     | **Description**                                                                                  |
|----------------|--------------------------------------------------------------------------------------------------|
| `HOUR_12`      | 12-hour format with AM/PM suffix (e.g., 5:00 AM).                                               |
| `HOUR_12_NS`   | 12-hour format without AM/PM suffix (e.g., 5:00).                                               |
| `HOUR_24`      | 24-hour format (e.g., 17:00).                                                                   |
| `FLOATING`     | Floating-point representation of hours (e.g., 5.5 for 5:30).                                    |

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
