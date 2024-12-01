package com.orbitalsonic.offlineprayertime.utils

import android.content.SharedPreferences

private const val KEY_LATITUDE = "user_latitude"
private const val KEY_LONGITUDE = "user_longitude"
private const val KEY_TIME_FORMAT = "time_format"
private const val KEY_CALCULATION_METHOD = "calc_method"
private const val KEY_ASR_METHOD = "asr_method"
private const val KEY_HIGH_LATITUDE_ADJUSTMENT = "high_lat_adj"

class UserSettingsManager(private val sharedPreferences: SharedPreferences) {

    var latitude: Double
        get() = Double.fromBits(sharedPreferences.getLong(KEY_LATITUDE, 0L))
        set(value) {
            sharedPreferences.edit().putLong(KEY_LATITUDE, value.toBits()).apply()
        }

    var longitude: Double
        get() = Double.fromBits(sharedPreferences.getLong(KEY_LONGITUDE, 0L))
        set(value) {
            sharedPreferences.edit().putLong(KEY_LONGITUDE, value.toBits()).apply()
        }

    var timeFormat: Int
        get() = sharedPreferences.getInt(KEY_TIME_FORMAT, 24) // Default: 24-hour format
        set(value) {
            sharedPreferences.edit().putInt(KEY_TIME_FORMAT, value).apply()
        }

    var calculationMethod: Int
        get() = sharedPreferences.getInt(KEY_CALCULATION_METHOD, 1) // Default: Standard
        set(value) {
            sharedPreferences.edit().putInt(KEY_CALCULATION_METHOD, value).apply()
        }

    var asrMethod: Int
        get() = sharedPreferences.getInt(KEY_ASR_METHOD, 1) // Default: Shafi'i
        set(value) {
            sharedPreferences.edit().putInt(KEY_ASR_METHOD, value).apply()
        }

    var highLatitudeAdjustment: Int
        get() = sharedPreferences.getInt(KEY_HIGH_LATITUDE_ADJUSTMENT, 3) // Default: Angle-based
        set(value) {
            sharedPreferences.edit().putInt(KEY_HIGH_LATITUDE_ADJUSTMENT, value).apply()
        }
}
