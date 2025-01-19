package com.example.money_manager_app.data.model

import com.example.money_manager_app.data.model.entity.Account

data class AccountWithWalletItem (
    val account: Account,
    val walletItems: List<WalletItem> = emptyList()
)