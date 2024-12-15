
# Offline Prayer Time Library

This library provides an easy way to get Islamic prayer times based on geographic location (latitude and longitude) for daily, monthly, and yearly time calculations. It handles prayer time calculations using popular methods such as **Juristic Method** and **High Latitude Adjustment**.

The library is designed to work offline and handle prayer time fetching and calculations for multiple locations, dates, and calculation methods.

## Features
- **Daily Prayer Times**: Get prayer times for the current day based on location.
- **Monthly Prayer Times**: Get prayer times for the entire month.
- **Yearly Prayer Times**: Get prayer times for the whole year.
- **Customizable Calculation**: Supports various calculation methods and adjustments for high-latitude locations.
- **Support for Multiple Time Formats**: Choose between 12-hour and 24-hour time formats.

## Setup

To include this library in your Android project, follow these steps:

1. **Add Dependency**:
   Add the following to your `build.gradle` dependencies:

   ```groovy
   implementation 'com.orbitalsonic:offlineprayertime:1.0.0'
   ```

2. **Sync Gradle**:
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

### PrayerTimeRepository

`PrayerTimeRepository` is responsible for calculating and fetching prayer times. It handles fetching prayer times from the server or local storage and supports different calculation methods. You can use it directly if you need custom data handling.

#### Example (Repository):

```kotlin
class PrayerTimeRepository {

    suspend fun getDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<PrayerTimesItem> {
        // Implement logic to get daily prayer times based on parameters
        return listOf() // Return prayer times
    }

    suspend fun getMonthlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<PrayerTimesItem>> {
        // Implement logic to get monthly prayer times
        return listOf() // Return monthly prayer times
    }

    suspend fun getYearlyPrayerTimes(
        latitude: Double,
        longitude: Double,
        year: Int,
        highLatitudeAdjustment: HighLatitudeAdjustment,
        juristicMethod: JuristicMethod,
        organizationStandard: OrganizationStandard,
        timeFormat: TimeFormat
    ): List<List<PrayerTimesItem>> {
        // Implement logic to get yearly prayer times
        return listOf() // Return yearly prayer times
    }
}
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
