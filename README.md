
[![](https://jitpack.io/v/orbitalsonic/opt.svg)](https://jitpack.io/#orbitalsonic/opt)

# OPT (Offline Prayer Time)

This library provides an easy way to get Islamic prayer times based on geographic location (latitude and longitude) for daily, monthly, and yearly time calculations. It handles prayer time calculations using popular methods such as **Juristic Method** and **High Latitude Adjustment**.

The library is designed to work offline and handle prayer time fetching and calculations for multiple locations, dates, and calculation methods.

## Features
- **Daily Prayer Times**: Get prayer times for the current day based on location.
- **Monthly Prayer Times**: Get prayer times for the entire month.
- **Yearly Prayer Times**: Get prayer times for the whole year.
- **Customizable Calculation**: Supports various calculation methods and adjustments for high-latitude locations.
- **Support for Multiple Time Formats**: Choose between 12-hour and 24-hour time formats.

## Setup

1. **Step 1**

Add maven repository in the project-level `build.gradle` or in the latest project `settings.gradle` file.

```groovy
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
```  

2. **Step 2**

Add OPT dependency in the app-level `build.gradle`. Replace `x.x.x` with the latest version:  
[![](https://jitpack.io/v/orbitalsonic/opt.svg)](https://jitpack.io/#orbitalsonic/opt)

```groovy
    dependencies {
             implementation 'com.github.orbitalsonic:opt:x.x.x'
    }
```  

3. **Sync Gradle**:
   Sync the Gradle project to download the dependency.

## Usage

### PrayerTimeManager

To access prayer times, use the `PrayerTimeManager` class. Here's how you can fetch prayer times based on your location and preferences.

#### Initialize `PrayerTimeManager`:

```kotlin
val prayerTimeManager = PrayerTimeManager()
```

#### Fetch Daily Prayer Times:

To fetch prayer times for the current day, use the `fetchingPrayerTimes` method with `TimeFrequency.DAILY`.

```kotlin
val latitude = 40.7128 // Example latitude (New York)
val longitude = -74.0060 // Example longitude (New York)
val highLatitudeAdjustment = HighLatitudeAdjustment.NONE
val juristicMethod = JuristicMethod.HANAFI
val organizationStandard = OrganizationStandard.KARACHI
val timeFormat = TimeFormat.HOUR_12

prayerTimeManager.fetchingPrayerTimes(
    latitude = latitude,
    longitude = longitude,
    highLatitudeAdjustment = highLatitudeAdjustment,
    juristicMethod = juristicMethod,
    organizationStandard = organizationStandard,
    timeFormat = timeFormat,
    timeFrequency = TimeFrequency.DAILY
)
```

#### Fetch Monthly Prayer Times:

To fetch prayer times for the current month, use the `fetchingPrayerTimes` method with `TimeFrequency.MONTHLY`.

```kotlin
prayerTimeManager.fetchingPrayerTimes(
    latitude = latitude,
    longitude = longitude,
    highLatitudeAdjustment = highLatitudeAdjustment,
    juristicMethod = juristicMethod,
    organizationStandard = organizationStandard,
    timeFormat = timeFormat,
    timeFrequency = TimeFrequency.MONTHLY
)
```

#### Fetch Yearly Prayer Times:

To fetch prayer times for the current year, use the `fetchingPrayerTimes` method with `TimeFrequency.YEARLY`.

```kotlin
prayerTimeManager.fetchingPrayerTimes(
    latitude = latitude,
    longitude = longitude,
    highLatitudeAdjustment = highLatitudeAdjustment,
    juristicMethod = juristicMethod,
    organizationStandard = organizationStandard,
    timeFormat = timeFormat,
    timeFrequency = TimeFrequency.YEARLY
)
```

### Observing Prayer Times in MainActivity

You can observe prayer times in your `MainActivity` using LiveData.

```kotlin
// Observe today's prayer times
prayerTimeManager.prayerTimeTodayLiveData.observe(this, Observer { prayerTimes ->
    // Update UI with today's prayer times
    // prayerTimes is a list of PrayerTimesItem objects
})

// Similarly, you can observe monthly and yearly prayer times
prayerTimeManager.prayerTimeMonthlyLiveData.observe(this, Observer { monthlyPrayerTimes ->
    // Update UI with monthly prayer times
})

prayerTimeManager.prayerTimeYearlyLiveData.observe(this, Observer { yearlyPrayerTimes ->
    // Update UI with yearly prayer times
})
```

## Configuration Options

The library provides several enums for customization. Developers can use these enums to adjust prayer time calculations according to their requirements.

### High Latitude Adjustment
- **NONE**: No adjustment applied for high latitudes.
- **ANGLE_BASED**: Uses the angle of the sun below the horizon to define Fajr and Isha times.
- **MID_NIGHT**: Divides the night into two equal halves for estimating Fajr and Isha times.
- **ONE_SEVENTH**: Divides the night into seven parts, with Fajr at one-seventh and Isha at six-sevenths of the night.

### Juristic Method
- **SHAFII**: Shafi'i school of thought. Asr begins when the shadow of an object equals its height.
- **HANAFI**: Hanafi school of thought. Asr begins when the shadow of an object is twice its height.

### Organization Standard
- **MAKKAH**: Umm al-Qura method used in Saudi Arabia. Fajr angle is 18.5° and Isha is a fixed interval of 90 minutes.
- **EGYPT**: Egyptian General Authority of Survey. Fajr angle is 19.5° and Isha angle is 17.5°.
- **TEHRAN**: Institute of Geophysics, University of Tehran. Fajr angle is 17.7° and Isha angle is 14°.
- **JAFARI**: Ithna Ashari method. Fajr angle is 16° and Isha angle is 14°.
- **KARACHI**: University of Islamic Sciences, Karachi. Fajr and Isha angles are both 18°.
- **ISNA**: Islamic Society of North America. Fajr and Isha angles are both 15°.
- **MWL**: Muslim World League. Fajr angle is 18° and Isha angle is 17°.
- **CUSTOM**: User-defined custom settings for angles or intervals.

### Time Format
- **HOUR_12**: 12-hour format with AM/PM suffix (e.g., 5:00 AM).
- **HOUR_12_NS**: 12-hour format without AM/PM suffix (e.g., 5:00).
- **HOUR_24**: 24-hour format (e.g., 17:00).
- **FLOATING**: Floating-point representation of hours (e.g., 5.5 for 5:30).

### Time Frequency
- **DAILY**: Get prayer times for the current day.
- **MONTHLY**: Get prayer times for the current month.
- **YEARLY**: Get prayer times for the current year.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
