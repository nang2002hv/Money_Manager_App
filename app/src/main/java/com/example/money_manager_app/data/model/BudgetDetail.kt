package com.example.money_manager_app.data.model

import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.BudgetWithCategory
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.enums.PeriodType

data class BudgetDetail(
    val id: Long = 0,
    val accountId: Long,
    val name: String,
    val colorId: Int? = R.color.color_1,
    val amount: Double,
    val periodDateStart: Long,
    val periodType: PeriodType,
    val categories: List<Category>
)

fun BudgetWithCategory.toBudgetDetail() = BudgetDetail(
    budget.id,
    budget.accountId,
    budget.name,
    budget.colorId,
    budget.amount,
    budget.startDate,
    budget.periodType,
    categories

)