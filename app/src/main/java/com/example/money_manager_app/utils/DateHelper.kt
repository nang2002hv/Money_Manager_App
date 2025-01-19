package com.example.money_manager_app.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateHelper {
     fun isNotSameYear(date: Date?): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        val calendar2: Calendar = Calendar.getInstance()
        calendar.setTime(date)
        return calendar.get(1) !== calendar2.get(1)
    }

    fun isSameDay(date1: Long, date2: Long): Boolean {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar.timeInMillis = date1
        calendar2.timeInMillis = date2
        return calendar[1] == calendar2[1] && calendar[6] == calendar2[6]
    }

    fun getFormattedDate(context: Context?, date: Date): String {
        return SimpleDateFormat(
            DateFormat.getBestDateTimePattern(
                Locale.getDefault(),
                "yyyy/MM/dd"
            ), Locale.getDefault()
        ).format(date.time)
    }

    fun getDateWeek(date: Date): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startOfWeekString = dateFormat.format(startOfWeek)
        val endOfWeekString = dateFormat.format(endOfWeek)
        return Pair(startOfWeekString, endOfWeekString)
    }

    fun getDateMonth(date: Date): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 0)
        val endOfMonth = calendar.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return Pair(dateFormat.format(startOfMonth), dateFormat.format(endOfMonth))
    }



    fun getDateYear(date: Date): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        val startOfYear = calendar.time
        calendar.add(Calendar.YEAR, 1)
        calendar.set(Calendar.DAY_OF_YEAR, 0)
        val endOfYear = calendar.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return Pair(dateFormat.format(startOfYear), dateFormat.format(endOfYear))
    }




}