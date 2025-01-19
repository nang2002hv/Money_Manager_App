package com.example.money_manager_app.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TimeType : Parcelable {
    DAILY, WEEKLY, MONTHLY, YEARLY, ALL, CUSTOM
}