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

        binding.btnDailyPrayerTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDailyPrayerTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
        binding.btnMonthlyPrayerTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMonthlyPrayerTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
        binding.btnYearlyPrayerTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToYearlyPrayerTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
        binding.btnDailyFastingTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDailyFastingTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
        binding.btnMonthlyFastingTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMonthlyFastingTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
        binding.btnYearlyFastingTime.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToYearlyFastingTimeFragment()
            navigateTo(R.id.homeFragment, action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}