package com.example.money_manager_app.utils

import com.example.money_manager_app.R

object IconUtils {
    private val walletIcons = listOf(
        R.drawable.wallet_1,
        R.drawable.wallet_2,
        R.drawable.wallet_3,
        R.drawable.wallet_4,
        R.drawable.wallet_6,
        R.drawable.wallet_7,
        R.drawable.wallet_8,
        R.drawable.wallet_9,
        R.drawable.wallet_10,
        R.drawable.wallet_11,
        R.drawable.wallet_12,
        R.drawable.wallet_13,
        R.drawable.wallet_14,
        R.drawable.wallet_15,
        R.drawable.wallet_16,
        R.drawable.wallet_17,
        R.drawable.wallet_18,
        R.drawable.wallet_19,
        R.drawable.wallet_20,
        R.drawable.wallet_21,
        R.drawable.wallet_22,
    )

    fun getWalletIconList(): List<Int> {
        return walletIcons
    }
}