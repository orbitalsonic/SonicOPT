package com.orbitalsonic.offlineprayertime.manager

import com.orbitalsonic.offlineprayertime.models.PrayerTimesItem
import com.orbitalsonic.offlineprayertime.models.SehriIftaarItem
import com.orbitalsonic.offlineprayertime.utils.UserSettingsManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerTimeManger(private val userSettings: UserSettingsManager) {

    fun getPrayerTimesForToday(): List<PrayerTimesItem> {
        val prayerTimes = mutableListOf<PrayerTimesItem>()
        val dummyTimes = listOf(
            "05:00",
            "06:30",
            "12:15",
            "15:30",
            "18:00",
            "19:30"
        ) // Replace with calculation logic

        val names = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        val now = Calendar.getInstance()
        val formatter = SimpleDateFormat(
            if (userSettings.timeFormat == 24) "HH:mm" else "hh:mm a",
            Locale.getDefault()
        )

        dummyTimes.forEachIndexed { index, time ->
            val parsedTime = formatter.parse(time)
            val isNow = now.time == parsedTime

            prayerTimes.add(PrayerTimesItem(names[index], formatter.format(parsedTime), isNow))
        }

        return prayerTimes
    }

    fun getSehriAndIftaarForToday(): SehriIftaarItem {
        val prayerTimes = getPrayerTimesForToday()
        val fajr = prayerTimes.firstOrNull { it.prayerName == "Fajr" }?.prayerTime ?: "N/A"
        val maghrib = prayerTimes.firstOrNull { it.prayerName == "Maghrib" }?.prayerTime ?: "N/A"
        return SehriIftaarItem(sehriTime = fajr, iftaarTime = maghrib)
    }

    fun getMonthlyPrayerTimes(): List<List<PrayerTimesItem>> {
        val monthPrayerTimes = mutableListOf<List<PrayerTimesItem>>()

        for (day in 1..30) {
            val dailyTimes = getPrayerTimesForToday() // Replace with actual calculation per day
            monthPrayerTimes.add(dailyTimes)
        }

        return monthPrayerTimes
    }

    fun getMonthlySehriIftaarTimes(): List<SehriIftaarItem> {
        val monthSehriIftaar = mutableListOf<SehriIftaarItem>()

        for (day in 1..30) {
            monthSehriIftaar.add(getSehriAndIftaarForToday())
        }

        return monthSehriIftaar
    }
}
