package com.example.repairservicesapp.util

import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat

object StatusBarUtils {
    @JvmStatic
    fun setStatusBarColor(window: Window, statusBarColor: Int, navigationBarColor: Int) {
        val decorView = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = true
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
    }
}