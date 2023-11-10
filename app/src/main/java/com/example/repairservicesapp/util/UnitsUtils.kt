package com.example.repairservicesapp.util

import android.content.Context

object UnitsUtils {
    @JvmStatic
    fun dpToPx(dp: Int, context: Context): Int {
        val density: Float = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}