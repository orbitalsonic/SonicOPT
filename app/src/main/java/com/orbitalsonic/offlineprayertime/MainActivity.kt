package com.orbitalsonic.offlineprayertime

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.manager.PrayerTimeManager

class MainActivity : AppCompatActivity() {
    val latitude: Double = 33.6995
    val longitude: Double = 73.0363

    private val prayerTimeManager = PrayerTimeManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch and display daily prayer times
        prayerTimeManager.fetchDailyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
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

        // Fetch and display monthly prayer times
        prayerTimeManager.fetchMonthlyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
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

        // Fetch and display yearly prayer times
        prayerTimeManager.fetchYearlyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
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

        // Fetch and display daily fasting times
        prayerTimeManager.fetchDailyFastingTimes(
            latitude = latitude,
            longitude = longitude,
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

        // Fetch and display monthly fasting times
        prayerTimeManager.fetchMonthlyFastingTimes(
            latitude = latitude,
            longitude = longitude,
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

        // Fetch and display yearly fasting times
        prayerTimeManager.fetchYearlyFastingTimes(
            latitude = latitude,
            longitude = longitude,
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
    }
}
