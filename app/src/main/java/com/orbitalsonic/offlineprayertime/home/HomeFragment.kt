package com.orbitalsonic.offlineprayertime.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orbitalsonic.offlineprayertime.R
import com.orbitalsonic.offlineprayertime.databinding.FragmentHomeBinding
import com.orbitalsonic.offlineprayertime.utils.navigateTo

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnDailyPrayerTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDailyPrayerTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnMonthlyPrayerTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToMonthlyPrayerTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnYearlyPrayerTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToYearlyPrayerTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnDatePrayerTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDatePrayerTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnDateRangePrayerTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDateRangePrayerTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnDailyFastingTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDailyFastingTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnMonthlyFastingTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToMonthlyFastingTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnYearlyFastingTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToYearlyFastingTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnDateFastingTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDateFastingTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
            btnDateRangeFastingTime.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDateRangeFastingTimeFragment()
                navigateTo(R.id.homeFragment, action)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}