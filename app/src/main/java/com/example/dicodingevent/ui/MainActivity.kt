package com.example.dicodingevent.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.example.dicodingevent.ui.finished.FinishedEventFragment
import com.example.dicodingevent.ui.home.HomeFragment
import com.example.dicodingevent.ui.upcoming.UpcomingEventFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_upcoming -> {
                    replaceFragment(UpcomingEventFragment())
                    true
                }
                R.id.nav_finished -> {
                    replaceFragment(FinishedEventFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}