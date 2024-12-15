package com.orbitalsonic.offlineprayertime

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.orbitalsonic.offlineprayertime.enums.HighLatitudeAdjustment
import com.orbitalsonic.offlineprayertime.enums.JuristicMethod
import com.orbitalsonic.offlineprayertime.enums.OrganizationStandard
import com.orbitalsonic.offlineprayertime.enums.TimeFormat
import com.orbitalsonic.offlineprayertime.enums.TimeFrequency
import com.orbitalsonic.offlineprayertime.manager.PrayerTimeManager
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val latitude:Double = 33.6995
    val longitude:Double = 73.0363

    private val prayerTimeManager = PrayerTimeManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prayerTimeManager.fetchingPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            highLatitudeAdjustment = HighLatitudeAdjustment.NONE,
            juristicMethod = JuristicMethod.HANAFI,
            organizationStandard = OrganizationStandard.KARACHI,
            timeFormat = TimeFormat.HOUR_12,
            timeFrequency = TimeFrequency.DAILY
        )

        prayerTimeManager.prayerTimeTodayLiveData.observe(this) { prayerTimes ->
            // Update UI with prayer times
            prayerTimes.forEach {
                Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
            }
        }

        prayerTimeManager.prayerTimeMonthlyLiveData.observe(this) { prayerTimes ->
            prayerTimes.forEachIndexed { index, dailyPrayerList ->
                Log.d("PrayerTimeTag", "----Day ${index+1}----")
                dailyPrayerList.forEach {
                    Log.d("PrayerTimeTag", "${it.prayerName}: ${it.prayerTime}")
                }
            }
        }

        prayerTimeManager.prayerTimeYearlyLiveData.observe(this) { prayerTimes ->
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

    }
}