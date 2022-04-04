package com.example.locationapp

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(index: Int): Fragment {
        when (index) {
            0 -> return MapFragment()
            1 -> return LocationsFragment()
            2 -> return MapFragment()
        }
        return MapFragment()
    }
}