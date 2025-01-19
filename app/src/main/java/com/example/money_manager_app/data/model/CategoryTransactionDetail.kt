package com.example.money_manager_app.data.model

import android.os.Parcelable
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.Transfer
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryTransactionDetail(
    var iconId : Int = R.drawable.all_category,
    var name: String = "",
    var totalCategoryTransaction :Int = 0,
    var totalAmount: Double = 0.0,
    var listTransfer: List<Transfer> = listOf()
) : Parcelable