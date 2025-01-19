package com.example.money_manager_app.utils

import android.content.Context


object SharePreferenceHelper {
    fun getFirstDayOfWeek(c: Context): Int {
        return c.getSharedPreferences("WalletPreferences", 0).getInt("dayOfWeek", 1)
    }
}