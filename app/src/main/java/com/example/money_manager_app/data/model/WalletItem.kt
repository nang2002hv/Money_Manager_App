package com.example.money_manager_app.data.model

import com.example.money_manager_app.data.model.entity.Wallet

data class WalletItem(
    val wallet: Wallet,
    val startingAmount: Double,
    val endingAmount: Double,
)

data class DateRangeAmount(
    val startingAmount: Double,
    val endingAmount: Double,
)

fun WalletItem.toWallet() = wallet