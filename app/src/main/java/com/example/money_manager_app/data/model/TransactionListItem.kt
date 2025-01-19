package com.example.money_manager_app.data.model

sealed class TransactionListItem {
    data class DateHeader(
        val dayOfMonth: String,
        val dayOfWeek: String,
        val monthYear: String,
        val total: Double
    ) : TransactionListItem()

    data class TransactionItem(val transaction: Transaction) : TransactionListItem()
}