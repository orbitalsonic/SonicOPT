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
Include the OPT library in your app-level build script (`build.gradle` or `build.gradle.kts`). Replace `x.x.x` with the latest version:

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

##### Fetch Daily Prayer Times
```kotlin
prayerTimeManager.fetchingPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.DAILY
)
```

##### Fetch Monthly Prayer Times
```kotlin
prayerTimeManager.fetchingPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.MONTHLY
)
```

##### Fetch Yearly Prayer Times
```kotlin
prayerTimeManager.fetchingPrayerTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.YEARLY
)
```

#### Observing Prayer Times in Required Activity or Fragment

##### Observe Today's Prayer Times
```kotlin
prayerTimeManager.prayerTimeTodayLiveData.observe(this) { prayerTimes ->
    // Example Logs: You can change according to your UI
    prayerTimes.forEach {
        Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
    }
}
```

##### Observe Monthly Prayer Times
```kotlin
prayerTimeManager.prayerTimeMonthlyLiveData.observe(this) { prayerTimes ->
    // Example Logs: You can change according to your UI
    prayerTimes.forEachIndexed { index, dailyPrayerList ->
        Log.d("PrayerTimeTag", "----Day ${index+1}----")
        dailyPrayerList.forEach {
            Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
        }
    }
}
```

##### Observe Yearly Prayer Times
```kotlin
prayerTimeManager.prayerTimeYearlyLiveData.observe(this) { prayerTimes ->
    // Example Logs: You can change according to your UI
    prayerTimes.forEachIndexed { index, monthlyPrayerList ->
        Log.d("PrayerTimeTag", "--------Month ${index+1}--------")
        monthlyPrayerList.forEachIndexed { index, dailyPrayerList ->
            Log.d("PrayerTimeTag", "--------Day ${index+1}--------")
            dailyPrayerList.forEach {
                Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
            }
        }
    }
}
```

---

### Fasting Time Manager
To fetch fasting times, use the `PrayerTimeManager` class in conjunction with fasting-related methods and LiveData. The following examples demonstrate its usage.

#### Fetching Fasting Times

##### Note
The arguments passed for fetching fasting and prayer times are defaults. You must update the latitude, longitude, and other parameters as per your specific requirements.

##### Fetch Daily Fasting Times
```kotlin
prayerTimeManager.fetchingFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.DAILY
)
```

##### Fetch Monthly Fasting Times
```kotlin
prayerTimeManager.fetchingFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.MONTHLY
)
```

##### Fetch Yearly Fasting Times
```kotlin
prayerTimeManager.fetchingFastingTimes(
    latitude = 40.7128, // Example latitude (New York)
    longitude = -74.0060, // Example longitude (New York)
    highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    juristicMethod = JuristicMethod.HANAFI,
    organizationStandard = OrganizationStandard.KARACHI,
    timeFormat = TimeFormat.HOUR_12,
    timeFrequency = TimeFrequency.YEARLY
)
```

#### Observing Fasting Times in Required Activity or Fragment

##### Observe Today's Fasting Times
```kotlin
prayerTimeManager.fastingTimeTodayLiveData.observe(this) { fastingTimes ->
    // Example Logs: You can change according to your UI
    Log.d("FastingTimeTag", "${fastingTimes.startTime} to ${fastingTimes.endTime}")
}
```

##### Observe Monthly Fasting Times
```kotlin
prayerTimeManager.fastingTimeMonthlyLiveData.observe(this) { monthlyFastingTimes ->
    // Example Logs: You can change according to your UI
    monthlyFastingTimes.forEachIndexed { index, fastingItem ->
        Log.d("FastingTimeTag", "----Day ${index+1}----")
        Log.d("FastingTimeTag", "${fastingItem.startTime} to ${fastingItem.endTime}")
    }
}
```

##### Observe Yearly Fasting Times
```kotlin
prayerTimeManager.fastingTimeYearlyLiveData.observe(this) { yearlyFastingTimes ->
   // Example Logs: You can change according to your UI
   yearlyFastingTimes.forEachIndexed { index, monthlyFastingList ->
      Log.d("FastingTimeTag", "--------Month ${index+1}--------")
      monthlyFastingList.forEachIndexed { index, fastingItem ->
         Log.d("FastingTimeTag", "----Day ${index+1}----")
         Log.d("FastingTimeTag", "${fastingItem.startTime} to ${fastingItem.endTime}")
      }
   }
}
```

---

## Configuration Options

### High Latitude Adjustment
- **NONE**: No adjustment for high latitudes.
- **ANGLE_BASED**: Defines Fajr and Isha times based on the sun's angle below the horizon.
- **MID_NIGHT**: Divides the night into two halves for Fajr and Isha.
- **ONE_SEVENTH**: Divides the night into seven parts.

### Juristic Method
- **SHAFII**: Asr begins when the shadow of an object equals its height.
- **HANAFI**: Asr begins when the shadow of an object is twice its height.

### Organization Standard
- **MAKKAH**: Umm al-Qura method (Saudi Arabia).
- **EGYPT**: Egyptian General Authority of Survey.
- **TEHRAN**: University of Tehran.
- **KARACHI**: University of Islamic Sciences, Karachi.
- **ISNA**: Islamic Society of North America.
- **MWL**: Muslim World League.
- **CUSTOM**: User-defined settings.

### Time Format
- **HOUR_12**: 12-hour format with AM/PM.
- **HOUR_12_NS**: 12-hour format without AM/PM.
- **HOUR_24**: 24-hour format.
- **FLOATING**: Floating-point format (e.g., 5.5 for 5:30).

---

## Contributing
Contributions are welcome! Fork the repository, make changes, and submit a pull request.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
