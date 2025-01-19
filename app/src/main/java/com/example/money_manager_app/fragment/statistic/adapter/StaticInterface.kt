package com.example.money_manager_app.fragment.statistic.adapter

import com.example.money_manager_app.utils.TimeType
import java.util.Date

interface StaticInterface {
    fun onClickTime(timeType : TimeType)
    fun onClickTime(startDate : Date, endDate : Date)
}