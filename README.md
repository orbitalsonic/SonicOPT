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

Add maven repository in project level build.gradle or in latest project setting.gradle file
```
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
```  

1. **Step 2**

Add SonicInApp dependencies in App level build.gradle. Replace x.x.x with the latest version [![](https://jitpack.io/v/orbitalsonic/opt.svg)](https://jitpack.io/#orbitalsonic/opt)
```
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

### High Latitude Adjustment
- **NONE**: No adjustments for high-latitude locations.
- **ANGLE_15**: Adjustment for locations above 15° latitude.
- **ANGLE_18**: Adjustment for locations above 18° latitude.

### Juristic Method
- **SHAFI**: Shafi'i method of calculation.
- **HANAFI**: Hanafi method of calculation.

### Organization Standard
- **ISNA**: Islamic Society of North America.
- **MWL**: Muslim World League.
- **KARACHI**: Karachi Standard.

### Time Format
- **HOUR_12**: 12-hour format (AM/PM).
- **HOUR_24**: 24-hour format.

### Time Frequency
- **DAILY**: Get prayer times for the current day.
- **MONTHLY**: Get prayer times for the current month.
- **YEARLY**: Get prayer times for the current year.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
