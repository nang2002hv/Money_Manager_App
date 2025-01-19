package com.example.money_manager_app.utils

import android.content.Context
import android.text.format.DateFormat
import com.example.money_manager_app.utils.SharePreferenceHelper.getFirstDayOfWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object CalendarHelper {

    fun getInitialDate(): Date {
        val calendar = Calendar.getInstance()
        calendar[5] = 1
        return calendar.time
    }


    fun getCalendarDay(date: Date?, i: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[5] = i
        return calendar.time
    }

    fun incrementDay(date: Date?, i: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(5, i)
        return calendar.time
    }
    fun incrementWeek(date: Date?, i: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(3, i)
        return calendar.time
    }

    fun incrementYear(date: Date?, i: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(1, i)
        return calendar.time
    }

    fun getFormattedDailyDate(date: Date?): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(
            DateFormat.getBestDateTimePattern(
                Locale.getDefault(),
                "dd MMMM yyyy"
            ), Locale.getDefault()
        ).format(calendar.time)
    }

    fun getFormattedWeeklyDate(context: Context?, date: Date?): String {
        val firstDayOfWeek = getFirstDayOfWeek(context!!)
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = 1
        calendar.time = date
        val i = calendar[7]
        if (firstDayOfWeek > i) {
            calendar.add(3, -1)
        }
        calendar[7] = firstDayOfWeek
        val calendar2 = Calendar.getInstance()
        calendar2.firstDayOfWeek = 1
        calendar2.time = date
        if (firstDayOfWeek > i) {
            calendar2.add(3, -1)
        }
        calendar2[7] = firstDayOfWeek
        calendar2.add(7, 6)
        var str = "dd MMM yyyy"
        val str2 = if (DateHelper.isNotSameYear(calendar.time)) "dd MMM yyyy" else "dd MMM"
        if (!DateHelper.isNotSameYear(calendar2.time)) {
            str = "dd MMM"
        }
        val bestDateTimePattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), str2)
        val bestDateTimePattern2 = DateFormat.getBestDateTimePattern(Locale.getDefault(), str)
        return SimpleDateFormat(
            bestDateTimePattern,
            Locale.getDefault()
        ).format(calendar.time) + " - " + SimpleDateFormat(
            bestDateTimePattern2,
            Locale.getDefault()
        ).format(calendar2.time)
    }

    fun getFormattedYearlyDate(date: Date?): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyy"),
            Locale.getDefault()
        ).format(calendar.time)
    }

    fun getFormattedCustomDate(context: Context?, startDate: Date, endDate: Date): String {
        if (DateHelper.isSameDay(startDate.time, endDate.time)) {
            return DateHelper.getFormattedDate(context, startDate)
        }
        return DateHelper.getFormattedDate(
            context,
            startDate
        ) + " - " + DateHelper.getFormattedDate(context, endDate)
    }


    fun incrementMonth(date: Date?, i: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(2, i)
        return calendar.time
    }

    fun getFormattedMonthlyDate(date: Date?): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM yyyy"),
            Locale.getDefault()
        ).format(calendar.time)
    }


    fun getDayOfMonth(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.getActualMaximum(5)
    }

    fun getDayOfWeek(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[5] = 1
        return calendar[7]
    }

    fun isSameMonth(date: Date?): Int {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar2.time = date
        if (calendar[1] == calendar2[1] && calendar[2] == calendar2[2]) {
            return calendar[5]
        }
        return -1
    }


}