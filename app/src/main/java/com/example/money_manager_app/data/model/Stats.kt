package com.example.money_manager_app.data.model

import com.example.money_manager_app.data.model.entity.enums.CategoryType

data class Stats(
    var name: String,
    var color: Int,
    var icon: Long,
    var amount: Double,
    var percent: Double,
    var id: Int,
    var trans: Int,
    var type: CategoryType,
    var categoryDefault: Int,
    var isDefault: Boolean
) {

}
