package com.orbitalsonic.offlineprayertime.fasting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orbitalsonic.offlineprayertime.databinding.FragmentYearlyFastingTimeBinding
import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.manager.PrayerTimeManager
import com.orbitalsonic.sonicopt.models.PrayerCustomAngle
import com.orbitalsonic.sonicopt.models.PrayerManualCorrection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class YearlyFastingTimeFragment : Fragment() {

    private var _binding: FragmentYearlyFastingTimeBinding? = null
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
        _binding = FragmentYearlyFastingTimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Fetch current year fasting times.
         *
         * - **Fasting Times**: Fasting times are derived from the Fajr and Maghrib prayer times.
         *   - **Sehri (Pre-Dawn Meal)**: Ends at the start time of the Fajr prayer.
         *   - **Iftar (Breaking Fast)**: Begins at the start time of the Maghrib prayer.
         *
         * - **Manual Corrections**: For `PrayerManualCorrection`, only allow manual corrections within the range of -59 to 59 minutes.
         *   If the correction is outside this range, it will default to 0 (no correction).
         *
         * - **Custom Angles**: If `PrayerTimeConvention` is set to CUSTOM, the method will use custom angles for Fajr and Isha prayers.
         *   The default custom angles are:
         *      - Fajr: 9.0°
         *      - Isha: 14.0°
         */
        prayerTimeManager.fetchCurrentYearFastingTimes(
            latitude = latitude,
            longitude = longitude,
            highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
            prayerTimeConvention = PrayerTimeConvention.KARACHI,
            timeFormat = TimeFormat.HOUR_12,
            prayerManualCorrection = PrayerManualCorrection(fajrMinute = 0,maghribMinute = 0),
            prayerCustomAngle = PrayerCustomAngle(fajrAngle = 9.0, ishaAngle = 14.0)
        ) { result ->
            result.onSuccess { fastingTimes ->
                logBuilder.appendLine("----Yearly Fasting Times----")
                logBuilder.appendLine("")

                fastingTimes.forEachIndexed { monthIndex, monthlyFastingList ->
                    logBuilder.appendLine("Month ${monthIndex + 1}")
                    logBuilder.appendLine("")

                    monthlyFastingList.forEachIndexed { dayIndex, fastingItem ->
                        val formattedDate = dateFormatter.format(Date(fastingItem.date))

                        logBuilder.appendLine("Day ${dayIndex + 1} - Date: $formattedDate")
                        logBuilder.appendLine("Sehri: ${fastingItem.sehriTime}, Iftar: ${fastingItem.iftaarTime}")
                        logBuilder.appendLine("")

                    }

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
