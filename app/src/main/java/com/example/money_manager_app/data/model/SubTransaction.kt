package com.example.money_manager_app.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

open class SubTransaction(
    override val id: Long,
    @DrawableRes override val iconId: Int,
    override val name: String,
    override val amount: Double,
    @ColorRes open val colorId: Int,
    override val accountId: Long,
    override val walletId: Long,
    override val date: Long,
    override val time: Long,
) : Transaction(id, iconId, name, amount, accountId, walletId, date, time)