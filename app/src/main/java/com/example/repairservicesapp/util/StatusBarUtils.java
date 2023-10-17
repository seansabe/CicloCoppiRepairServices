package com.example.repairservicesapp.util;

import android.view.View;
import android.view.Window;

import androidx.core.view.WindowInsetsControllerCompat;

public class StatusBarUtils {
    public static void setStatusBarColor(Window window, int statusBarColor, int navigationBarColor) {
        View decorView = window.getDecorView();
        WindowInsetsControllerCompat wic = new WindowInsetsControllerCompat(window, decorView);
        wic.setAppearanceLightStatusBars(true);
        window.setStatusBarColor(statusBarColor);
        window.setNavigationBarColor(navigationBarColor);
    }
}
