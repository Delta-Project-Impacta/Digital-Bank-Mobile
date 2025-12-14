package com.domleondev.deltabank.presentation.util

import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun AppCompatActivity.setupTransparentStatusBarNoPadding(
    rootViewId: Int,
    darkIcons: Boolean = true
) {
    val window = this.window

    when {
        // Android 10 e abaixo
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            @Suppress("DEPRECATION")
            window.statusBarColor = Color.TRANSPARENT
        }

        // Android 11+
        else -> {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            WindowInsetsControllerCompat(window, window.decorView)
                .isAppearanceLightStatusBars = darkIcons

            window.statusBarColor = Color.TRANSPARENT
        }
    }

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootViewId)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(
            systemBars.left,
            0,
            systemBars.right,
            systemBars.bottom
        )
        insets
    }
}
