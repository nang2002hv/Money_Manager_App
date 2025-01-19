package com.example.money_manager_app.data.model.entity.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GoalInputType : Parcelable {
    DEPOSIT,
    WITHDRAW
}