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

##### Note
The arguments passed for fetching prayer and fasting times are defaults. You must update the latitude, longitude, and other parameters as per your specific requirements.

##### Initialize `PrayerTimeManager`
```kotlin
val prayerTimeManager = PrayerTimeManager()
```

#### Fetch Daily Prayer Times
```kotlin
prayerTimeManager.fetchDailyPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerTimes ->
        prayerTimes.forEach {
            Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
        }
    }.onFailure { exception ->
        Log.e("PrayerTimeTag", "Error fetching daily prayer times", exception)
    }
}
```

#### Fetch Monthly Prayer Times
```kotlin
prayerTimeManager.fetchMonthlyPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerTimes ->
        prayerTimes.forEachIndexed { index, dailyPrayerList ->
            Log.d("PrayerTimeTag", "----Day ${index + 1}----")
            dailyPrayerList.forEach {
                Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
            }
        }
    }.onFailure { exception ->
        Log.e("PrayerTimeTag", "Error fetching monthly prayer times", exception)
    }
}
```

#### Fetch Yearly Prayer Times
```kotlin
prayerTimeManager.fetchYearlyPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { prayerTimes ->
        prayerTimes.forEachIndexed { monthIndex, monthlyPrayerList ->
            Log.d("PrayerTimeTag", "--------Month ${monthIndex + 1}--------")
            monthlyPrayerList.forEachIndexed { dayIndex, dailyPrayerList ->
                Log.d("PrayerTimeTag", "--------Day ${dayIndex + 1}--------")
                dailyPrayerList.forEach {
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

### Fasting Time Manager
To fetch fasting times, use the `PrayerTimeManager` class in conjunction with fasting-related methods and LiveData. The following examples demonstrate its usage.

#### Fetching Fasting Times

##### Note
The arguments passed for fetching fasting and prayer times are defaults. You must update the latitude, longitude, and other parameters as per your specific requirements.

### Fetching Fasting Times

#### Fetch Daily Fasting Times
```kotlin
prayerTimeManager.fetchDailyFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { fastingItem ->
        Log.d("FastingTimeTag", "Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching daily fasting times", exception)
    }
}
```

#### Fetch Monthly Fasting Times
```kotlin
prayerTimeManager.fetchMonthlyFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12
) { result ->
    result.onSuccess { fastingTimes ->
        fastingTimes.forEachIndexed { index, fastingItem ->
            Log.d("FastingTimeTag", "Day ${index + 1} -> Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
        }
    }.onFailure { exception ->
        Log.e("FastingTimeTag", "Error fetching monthly fasting times", exception)
    }
}
```

#### Fetch Yearly Fasting Times
```kotlin
prayerTimeManager.fetchYearlyFastingTimes(
   latitude = 40.7128, // Example latitude (New York)
   longitude = -74.0060, // Example longitude (New York)
   highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
   juristicMethod = JuristicMethod.HANAFI,
   organizationStandard = OrganizationStandard.KARACHI,
   timeFormat = TimeFormat.HOUR_12
) { result ->
   result.onSuccess { fastingTimes ->
      fastingTimes.forEachIndexed { monthIndex, monthlyFastingList ->
         Log.d("FastingTimeTag", "--------Month ${monthIndex + 1}--------")
         monthlyFastingList.forEachIndexed { dayIndex, fastingItem ->
            Log.d("FastingTimeTag", "Day ${dayIndex + 1} -> Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
         }
      }
   }.onFailure { exception ->
      Log.e("FastingTimeTag", "Error fetching yearly fasting times", exception)
   }
}
```

---

## Configuration Options

Below is a categorized list of configuration options, their parent enums, and descriptions:

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
