package com.domleondev.deltabank.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.fragments.FragmentChat
import com.domleondev.deltabank.presentation.fragments.FragmentExtract
import com.domleondev.deltabank.presentation.fragments.FragmentHome
import com.domleondev.deltabank.presentation.fragments.FragmentProfile
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.container,
            darkIcons = true
        )

        val navigation = findViewById<BottomNavigationView>(R.id.bottom_home_navigation)

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_Navigation_Home -> {
                    replaceFragment(FragmentHome.newInstance())
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

        val navContainer = findViewById<View>(R.id.bottom_nav_container)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->

            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(bars.left, 0, bars.right, 0)

            navContainer?.setPadding(0, 0, 0, bars.bottom)

            val newInsets = WindowInsetsCompat.Builder(insets)

                .setInsets(

                    WindowInsetsCompat.Type.systemBars(),

                    androidx.core.graphics.Insets.of(bars.left, bars.top, bars.right, 0)

                )

                .build()

            newInsets

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}