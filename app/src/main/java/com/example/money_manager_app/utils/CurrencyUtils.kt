package com.example.money_manager_app.utils

import android.content.Context
import android.util.Log
import com.example.money_manager_app.data.model.entity.enums.Currency

fun getCurrencyName(context: Context, currency: Currency): String {
    return context.getString(currency.nameRes)
}

fun getCurrencySymbol(context: Context, currency: Currency): String {
    return context.getString(currency.symbolRes)
}