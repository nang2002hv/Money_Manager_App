package com.example.money_manager_app.data.model

import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.DebtDetail
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.enums.DebtType

data class DebtItem(
    override val id: Long = 0,
    override val iconId: Int = R.drawable.wallet_6,
    override val name: String,
    override val amount: Double,
    override val accountId: Long,
    val type: DebtType,
    override val date: Long,
    override val time: Long,
    val description: String,
    override val walletId: Long,
    val colorId: Int = R.color.color_1,
    val debTransaction: List<DebtTransaction>
) : Transaction(id, iconId, name, amount, accountId, walletId, date, time)

fun DebtDetail.toDebtItem() = DebtItem(
    debt.id,
    debt.iconId,
    debt.name,
    debt.amount,
    debt.accountId,
    debt.type,
    debt.date,
    debt.time,
    debt.description,
    debt.walletId,
    debt.colorId,
    transactions
)
