package com.example.money_manager_app.utils

import android.util.Log
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.TransactionListItem
import com.example.money_manager_app.data.model.CalendarRecord
import com.example.money_manager_app.data.model.CalendarSummary
import com.example.money_manager_app.data.model.DateRangeAmount
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.TransferType

fun List<Transaction>.totalMoneyDay(listTransaction : List<Transaction>) : Double {
    var total = 0.0
    for (transaction in listTransaction) {
        when (transaction) {
            is GoalTransaction -> {
                total += if (transaction.type == GoalInputType.WITHDRAW) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Debt -> {
                total += if (transaction.type == DebtType.RECEIVABLE) {
                    -transaction.amount
                } else {
                    +transaction.amount
                }
            }

            is DebtTransaction -> {
                total += if (transaction.action == DebtActionType.DEBT_INCREASE || transaction.action == DebtActionType.DEBT_COLLECTION || transaction.action == DebtActionType.LOAN_INTEREST) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Transfer -> {
                total += if (transaction.typeOfExpenditure == TransferType.Income) {
                    +transaction.amount
                } else {
                    if (transaction.typeOfExpenditure == TransferType.Expense) {
                        -transaction.amount
                    } else {
                        -transaction.fee
                    }
                }
            }
        }
    }
    return total
}

fun List<Transaction>.groupRecordsByDate(): List<CalendarRecord> {
    val groupedList = mutableListOf<CalendarRecord>()
    val dailyTransaction = mutableListOf<Transaction>()
    val transactionList = this.sortedBy { it.date }
    var lastDate = transactionList.firstOrNull()?.date
    var totalIncome = 0.0
    var totalExpense = 0.0

    if (lastDate != null) {
        for(transaction in this.sortedBy { it.date }){

            if(transaction.date == lastDate){
                var total : Pair<Double, Double> = transaction.totalDailyTransactionIncomeAndExpense()
                totalIncome += total.first
                totalExpense += total.second

            } else {
                var daily = lastDate?.formatToDayOfMonth()
                groupedList.add(CalendarRecord(daily!!.toInt(), totalIncome, totalExpense))
                totalIncome = 0.0
                totalExpense = 0.0
                lastDate = transaction.date
                var total : Pair<Double, Double> = transaction.totalDailyTransactionIncomeAndExpense()
                totalIncome += total.first
                totalExpense += total.second
            }

        }
    }

    if (lastDate != null  || totalExpense != 0.0 !! || totalIncome != 0.0) {
        val daily = lastDate?.formatToDayOfMonth()
        groupedList.add(CalendarRecord(daily!!.toInt(), totalIncome, totalExpense))
    }

    return groupedList
}


fun totalCalendarSummary(listTransaction : List<Transaction>) : CalendarSummary {
    var totalIncome = 0.0
    var totalExpense = 0.0
    for(transaction in listTransaction){
        when(transaction){
            is Transfer -> {
                if(transaction.typeOfExpenditure == TransferType.Income){
                    totalIncome += transaction.amount
                } else {
                    if(transaction.typeOfExpenditure == TransferType.Expense){
                        totalExpense += transaction.amount
                    }
                }
            }

            is Debt -> {
                if (transaction.type == DebtType.RECEIVABLE) {
                    totalExpense += transaction.amount
                } else {
                    totalIncome += transaction.amount
                }
            }

            is DebtTransaction -> {
                if (transaction.action == DebtActionType.DEBT_INCREASE || transaction.action == DebtActionType.DEBT_COLLECTION) {
                    totalIncome += transaction.amount
                } else {
                    totalExpense += transaction.amount
                }
            }
        }
    }
    return CalendarSummary(totalIncome,totalExpense)
}

fun Transaction.totalDailyTransactionIncomeAndExpense(): Pair<Double, Double> {
    var dailyTotalIncome = 0.0
    var dailyTotalExpense = 0.0

    if (this is Debt) {
        if (this.type == DebtType.RECEIVABLE) {
            dailyTotalExpense += this.amount
        } else {
            dailyTotalIncome += this.amount
        }
    }

    if (this is DebtTransaction) {
        if (this.action in listOf(DebtActionType.DEBT_INCREASE, DebtActionType.DEBT_COLLECTION, DebtActionType.LOAN_INTEREST)) {
            dailyTotalIncome += this.amount
        } else {
            dailyTotalExpense += this.amount
        }
    }

    if (this is Transfer) {
        if (this.typeOfExpenditure == TransferType.Income) {
            dailyTotalIncome += this.amount
        } else if (this.typeOfExpenditure == TransferType.Expense) {
            dailyTotalExpense += this.amount
        }
        if (this.typeOfExpenditure == TransferType.Transfer) {
            dailyTotalExpense += this.fee
        }
    }

    return Pair(dailyTotalIncome, dailyTotalExpense)
}

fun List<Transaction>.groupTransactionsByDate(): List<TransactionListItem> {
    val groupedList = mutableListOf<TransactionListItem>()
    var lastDate: String? = null
    var dailyTotal = 0.0

    val sortedList = this.sortedByDescending { it.date }

    for (transaction in sortedList) {
        val dayOfMonth = transaction.date.formatToDayOfMonth()
        val dayOfWeek = transaction.date.formatToDayOfWeek()
        val monthYear = transaction.date.formatToMonthYear()

        if (dayOfMonth != lastDate) {
            if (lastDate != null) {
                groupedList.add(
                    TransactionListItem.DateHeader(
                        lastDate, dayOfWeek, monthYear, dailyTotal
                    )
                )
            }
            lastDate = dayOfMonth
            dailyTotal = 0.0
        }

        when (transaction) {
            is GoalTransaction -> {
                dailyTotal += if (transaction.type == GoalInputType.WITHDRAW) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Debt -> {
                dailyTotal += if (transaction.type == DebtType.RECEIVABLE) {
                    -transaction.amount
                } else {
                    +transaction.amount
                }
            }

            is DebtTransaction -> {
                val action = transaction.action
                dailyTotal += if (action in listOf(DebtActionType.DEBT_INCREASE, DebtActionType.DEBT_COLLECTION, DebtActionType.LOAN_INTEREST)) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Transfer -> {
                dailyTotal += if (transaction.typeOfExpenditure == TransferType.Income) {
                    +transaction.amount
                } else {
                    if (transaction.typeOfExpenditure == TransferType.Expense) {
                        -transaction.amount
                    } else {
                        -transaction.amount
                    }
                }
            }
        }

        groupedList.add(TransactionListItem.TransactionItem(transaction))
    }

    if (lastDate != null) {
        groupedList.add(
            TransactionListItem.DateHeader(
                lastDate,
                this.last().date.formatToDayOfWeek(),
                this.last().date.formatToMonthYear(),
                dailyTotal
            )
        )
    }

    return groupedList.reversed()
}

fun List<Transaction>.calculateCurrentWalletAmount(
    walletId: Long,
    startDate: Long,
    endDate: Long = System.currentTimeMillis()
): DateRangeAmount {
    val filteredList = this.filter { transaction ->
        transaction.date in (startDate + 1)..endDate
    }.sortedBy { it.date }

    val startingList = this.filter { transaction ->
        transaction.date <= startDate
    }.sortedBy { it.date }

    val startingAmount = startingList.calculateAmount(walletId)

    val endingAmount = filteredList.calculateAmount(walletId)

    return DateRangeAmount(
        startingAmount = startingAmount,
        endingAmount = startingAmount + endingAmount
    )
}

fun List<Transaction>.calculateCurrentWalletAmount(walletId: Long): DateRangeAmount {
    val startingAmount = this.calculateAmount(walletId)

    return DateRangeAmount(
        startingAmount = startingAmount,
        endingAmount = startingAmount
    )
}



fun List<Transaction>.calculateAmount(walletId: Long) : Double{
    var amount = 0.0
    for (transaction in this) {
        when (transaction) {
            is GoalTransaction -> {
                amount += if (transaction.type == GoalInputType.WITHDRAW) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Debt -> {
                amount += if (transaction.type == DebtType.RECEIVABLE) {
                    -transaction.amount
                } else {
                    transaction.amount
                }
            }

            is DebtTransaction -> {
                val action = transaction.action
                amount += if (action in listOf(DebtActionType.DEBT_INCREASE, DebtActionType.DEBT_COLLECTION, DebtActionType.LOAN_INTEREST)) {
                    transaction.amount
                } else {
                    -transaction.amount
                }
            }

            is Transfer -> {
                when (transaction.typeOfExpenditure) {
                    TransferType.Income -> {
                        amount += transaction.amount
                    }
                    TransferType.Expense -> {
                        amount -= transaction.amount
                    }
                    TransferType.Transfer -> {
                        if(transaction.walletId == walletId){
                            amount -= (transaction.amount+transaction.fee)
                        } else {
                            amount += transaction.amount
                        }
                    }
                }
            }
        }
    }
    return amount
}

