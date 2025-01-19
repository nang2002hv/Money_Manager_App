package com.example.money_manager_app.data.model

import com.example.money_manager_app.data.model.entity.enums.TransferType

data class AddTransfer(
    var walletId: Long,
    var toWallet: Long,
    var amount: Double,
    var name: String,
    var fee: Double,
    var description: String,
    var accountId: Long,
    var linkImg: String,
    var date: String,
    var time: String,
    var typeOfExpenditure: TransferType,
    var typeDebt: String,
    var iconId: Int,
    var colorId: Int,
    var typeIconWallet: String
) {
    constructor() : this(0, 0, 0.0, "", 0.0, "", 0, "", "", "", TransferType.Expense, "", 0, 0, "")
}
