package com.orbitalsonic.offlineprayertime.fasting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orbitalsonic.offlineprayertime.databinding.FragmentMonthlyFastingTimeBinding
import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.manager.PrayerTimeManager
import com.orbitalsonic.sonicopt.models.PrayerManualCorrection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MonthlyFastingTimeFragment : Fragment() {

    private var _binding: FragmentMonthlyFastingTimeBinding? = null
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
        _binding = FragmentMonthlyFastingTimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Fetch current yearly fasting times.
         * Fasting times are calculated based on Fajr and Maghrib prayers,
         * Sehri ends at Fajr start time, and Iftar begins at Maghrib time.
         */
        prayerTimeManager.fetchCurrentMonthFastingTimes(
            latitude = latitude,
            longitude = longitude,
            highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
            prayerTimeConvention = PrayerTimeConvention.KARACHI,
            timeFormat = TimeFormat.HOUR_12,
            prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0,maghribMinute = 0)
        ) { result ->
            result.onSuccess { fastingTimes ->
                logBuilder.appendLine("----Monthly Fasting Times----")
                logBuilder.appendLine("")

                fastingTimes.forEachIndexed { index, fastingItem ->
                    val formattedDate = dateFormatter.format(Date(fastingItem.date))
                    logBuilder.appendLine("Day ${index + 1} - Date: $formattedDate")
                    logBuilder.appendLine("Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
                    logBuilder.appendLine("")
                }

                updateTextView()
            }.onFailure { exception ->
                logBuilder.appendLine("Error fetching monthly fasting times: ${exception.message}")
                updateTextView()
            }
        }
    }

    private fun updateTextView() {
        binding.mtvFastingTime.text = logBuilder.toString()
        Log.d("PrayerTimeTag", logBuilder.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
