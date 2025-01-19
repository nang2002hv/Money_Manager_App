package com.example.money_manager_app.data.model


class CalendarSummary(var income: Double, var expense: Double) {
    val netIncome: Double
        get() = this.income + this.expense
}