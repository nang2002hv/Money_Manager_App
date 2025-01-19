package com.example.money_manager_app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toFormattedTimeString(format: String = "HH:mm"): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(this).toString()
}

fun Long.toDate(): String {
    return Date(this).toString()
}

fun Long.toFormattedDateString(format: String = "dd/MM/yyyy"): String {
    val date = Date(this)
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(date)
}

fun String.toDateTimestamp(format: String = "dd/MM/yyyy"): Long {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = sdf.parse(this)
    return date?.time ?: 0L
}

fun String.toTimeTimestamp(format: String = "HH:mm"): Long {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val time = sdf.parse(this)
    return time?.time ?: 0L
}

fun Long.formatToMonthYear(): String {
    val date = Calendar.getInstance().apply { timeInMillis = this@formatToMonthYear }.time
    return SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(date)
}

fun Long.formatToDayOfMonth(): String {
    val date = Calendar.getInstance().apply { timeInMillis = this@formatToDayOfMonth }.time
    return SimpleDateFormat("dd", Locale.getDefault()).format(date)
}

fun Long.formatToDayOfWeek(): String {
    val date = Calendar.getInstance().apply { timeInMillis = this@formatToDayOfWeek }.time
    return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
}

fun String.takeDayOfWeek(format: String = "dd/MM/yyyy"): Int {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = sdf.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun String.takeDayofMonth(format: String = "dd/MM/yyyy"): Int {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = sdf.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun String.takeMonthYear(format: String = "dd/MM/yyyy"): Int {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = sdf.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.MONTH) + 1
}





