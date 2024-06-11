package com.example.weatherapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.main.fragments.ForecastFragment
import com.example.weatherapp.main.fragments.HistoryFragment
import com.example.weatherapp.main.fragments.HomeFragment

class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            HomeFragment()
        } else if (position == 1) {
            ForecastFragment()
        } else {
            HistoryFragment()
        }
    }

}