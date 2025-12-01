package com.domleondev.deltabank.presentation.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.presentation.fragments.FragmentChat
import com.domleondev.deltabank.presentation.fragments.FragmentExtract
import com.domleondev.deltabank.presentation.fragments.FragmentHome
import com.domleondev.deltabank.presentation.fragments.FragmentProfile
import com.domleondev.deltabank.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val navigation = findViewById<BottomNavigationView>(R.id.bottom_home_navigation)

        navigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_nav_Navigation_Home -> {
                    replaceFragment(FragmentHome.Companion.newInstance())
                    true
                }
                R.id.bottom_nav_Navigation_Chat -> {
                    replaceFragment(FragmentChat())
                    true
                }
                R.id.bottom_nav_Navigation_Extract -> {
                    replaceFragment(FragmentExtract())
                    true
                }
                R.id.bottom_nav_Navigation_Profile -> {
                    replaceFragment(FragmentProfile())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            navigation.selectedItemId = R.id.bottom_nav_Navigation_Home
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}