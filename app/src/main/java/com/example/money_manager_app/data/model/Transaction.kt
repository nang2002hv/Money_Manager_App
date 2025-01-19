package com.example.money_manager_app.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
open class Transaction(
    open val id: Long,
    @DrawableRes open val iconId: Int?,
    open val name: String,
    open val amount: Double,
    open val accountId: Long,
    open val walletId: Long,
    open val date: Long,
    open val time: Long,
) : Parcelable