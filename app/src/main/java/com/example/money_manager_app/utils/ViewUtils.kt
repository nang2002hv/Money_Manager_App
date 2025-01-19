package com.example.money_manager_app.utils

import android.os.SystemClock
import android.view.View

private var lastClick: Long = 0

fun View.setOnSafeClickListener(
    duration: Long = Constants.Timing.DURATION_TIME_CLICKABLE,
    onClick: () -> Unit
) = setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClick >= duration) {
        onClick()
        lastClick = SystemClock.elapsedRealtime()
    }
}