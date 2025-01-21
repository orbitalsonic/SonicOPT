package com.orbitalsonic.offlineprayertime.prayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orbitalsonic.offlineprayertime.databinding.FragmentMonthlyPrayerTimeBinding
import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.manager.PrayerTimeManager
import com.orbitalsonic.sonicopt.models.PrayerCustomAngle
import com.orbitalsonic.sonicopt.models.PrayerManualCorrection
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class DateRangePrayerTimeFragment : Fragment() {

    private var _binding: FragmentMonthlyPrayerTimeBinding? = null
    private val binding get() = _binding!!

    // Sample Latitude & Longitude of Islamabad
    private val latitude: Double = 33.4979105
    private val longitude: Double = 73.0722461

    private val prayerTimeManager = PrayerTimeManager()

    private val logBuilder = StringBuilder()

    // Initialize SimpleDateFormat globally to save memory
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyPrayerTimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Fetch date range prayer times.
         *
         * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
         *   If the correction is outside this range, it will default to 0 (no correction).
         *
         * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
         *   The default custom angles are:
         *      - Fajr: 9.0°
         *      - Isha: 14.0°
         */

        // Create a Calendar instance and set it to June 5, 2025
        val startCalendar = GregorianCalendar(2025, Calendar.JUNE, 25)
        // Convert the Calendar instance to a Date object
        val startDate = startCalendar.time

        // Create a Calendar instance and set it to August 7, 2025
        val endCalendar = GregorianCalendar(2025, Calendar.JULY, 7)
        // Convert the Calendar instance to a Date object
        val endDate = endCalendar.time

        prayerTimeManager.fetchPrayerTimesForDateRange(
            latitude = latitude,
            longitude = longitude,
            startDate = startDate,
            endDate = endDate,
            highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
            asrJuristicMethod = AsrJuristicMethod.HANAFI,
            prayerTimeConvention = PrayerTimeConvention.KARACHI,
            timeFormat = TimeFormat.HOUR_12,
            prayerManualCorrection = PrayerManualCorrection(
                fajrMinute = 0,
                zuhrMinute = 0,
                asrMinute = 0,
                maghribMinute = 0,
                ishaMinute = 0
            ),
            prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
        ) { result ->
            result.onSuccess { prayerItems ->
                logBuilder.appendLine("----Date Range Prayer Times----")
                logBuilder.appendLine("")

                prayerItems.forEachIndexed { index, prayerItem ->
                    val formattedDate = dateFormatter.format(Date(prayerItem.date))
                    logBuilder.appendLine("Date: $formattedDate")
                    logBuilder.appendLine("")
                    prayerItem.prayerList.forEach {
                        logBuilder.appendLine("${it.prayerName}: ${it.prayerTime}")
                    }
                    logBuilder.appendLine("")
                }

                updateTextView()
            }.onFailure { exception ->
                logBuilder.appendLine("Error fetching monthly prayer times: ${exception.message}")
                updateTextView()
            }
        }
    }

    private fun updateTextView() {
        binding.mtvPrayerTime.text = logBuilder.toString()
        Log.d("PrayerTimeTag", logBuilder.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

