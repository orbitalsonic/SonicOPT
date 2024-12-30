package com.orbitalsonic.offlineprayertime.fasting

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orbitalsonic.offlineprayertime.databinding.FragmentYearlyFastingTimeBinding
import com.orbitalsonic.opt.enums.HighLatitudeAdjustment
import com.orbitalsonic.opt.enums.JuristicMethod
import com.orbitalsonic.opt.enums.OrganizationStandard
import com.orbitalsonic.opt.enums.TimeFormat
import com.orbitalsonic.opt.manager.PrayerTimeManager

class YearlyFastingTimeFragment : Fragment() {

    private var _binding: FragmentYearlyFastingTimeBinding? = null
    private val binding get() = _binding!!

    // Sample Latitude & Longitude of Islamabad
    private val latitude: Double = 33.6995
    private val longitude: Double = 73.0363

    private val prayerTimeManager = PrayerTimeManager()

    private val logBuilder = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYearlyFastingTimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                logBuilder.appendLine("----Yearly Fasting Times----")
                logBuilder.appendLine("")
                logBuilder.appendLine("")
                fastingTimes.forEachIndexed { monthIndex, monthlyFastingList ->
                    logBuilder.appendLine("Month ${monthIndex + 1}")
                    logBuilder.appendLine("")
                    logBuilder.appendLine("")
                    monthlyFastingList.forEachIndexed { dayIndex, fastingItem ->
                        logBuilder.appendLine("Day ${dayIndex + 1} -> Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
                    }

                    logBuilder.appendLine("")
                    logBuilder.appendLine("")
                }
                updateTextView()
            }.onFailure { exception ->
                logBuilder.appendLine("Error fetching yearly fasting times: ${exception.message}")
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