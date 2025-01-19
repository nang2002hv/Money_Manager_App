package com.example.money_manager_app.utils

import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.enums.CategoryType

object CategoryUtils {
    val listCategory: List<Category> = listOf(
        Category(1, "Award", 1, R.drawable.income_1, R.color.color_1, CategoryType.INCOME),
        Category(2, "Allowance", 1, R.drawable.income_2, R.color.color_2, CategoryType.INCOME),
        Category(3, "Dividend", 1, R.drawable.income_3, R.color.color_3, CategoryType.INCOME),
        Category(4, "Bonus", 1, R.drawable.income_4, R.color.color_4, CategoryType.INCOME),
        Category(5, "Investment", 1, R.drawable.income_5, R.color.color_5, CategoryType.INCOME),
        Category(6, "Lottery", 1, R.drawable.income_6, R.color.color_6, CategoryType.INCOME),
        Category(7, "Salary", 1, R.drawable.income_7, R.color.color_7, CategoryType.INCOME),
        Category(8, "Tips", 1, R.drawable.income_8, R.color.color_8, CategoryType.INCOME),
        Category(9, "Others", 1, R.drawable.income_9, R.color.color_9, CategoryType.INCOME),
        Category(10, "Bill", 1, R.drawable.expense_1, R.color.color_10, CategoryType.EXPENSE),
        Category(11, "Clothing", 1, R.drawable.expense_2, R.color.color_11, CategoryType.EXPENSE),
        Category(12, "Education", 1, R.drawable.expense_3, R.color.color_12, CategoryType.EXPENSE),
        Category(13, "Entertainment", 1, R.drawable.expense_4, R.color.color_13, CategoryType.EXPENSE),
        Category(14, "Fitness", 1, R.drawable.expense_5, R.color.color_14, CategoryType.EXPENSE),
        Category(15, "Food", 1, R.drawable.expense_6, R.color.color_15, CategoryType.EXPENSE),
        Category(16, "Furniture", 1, R.drawable.expense_7, R.color.color_16, CategoryType.EXPENSE),
        Category(17, "Gifts", 1, R.drawable.expense_8, R.color.color_17, CategoryType.EXPENSE),
        Category(18, "Health", 1, R.drawable.expense_9, R.color.color_18, CategoryType.EXPENSE),
        Category(19, "Pet", 1, R.drawable.expense_10, R.color.color_19, CategoryType.EXPENSE),
        Category(20, "Shopping", 1, R.drawable.expense_11, R.color.color_20, CategoryType.EXPENSE),
        Category(21, "Transportation", 1, R.drawable.expense_12, R.color.color_21, CategoryType.EXPENSE),
        Category(22, "Travel", 1, R.drawable.expense_13, R.color.color_22, CategoryType.EXPENSE),
        Category(23, "Others", 1, R.drawable.expense_14, R.color.color_23, CategoryType.EXPENSE),
        Category(24, "Transfer", 1, R.drawable.transfer_1, R.color.color_24, CategoryType.TRANSFER),
        Category(25, "REPAYMENT", 1, R.drawable.wallet_11, R.color.color_25, CategoryType.REPAYMENT),
        Category(26, "DEBT INCREASE", 1, R.drawable.wallet_11, R.color.color_26, CategoryType.DEBT_INCREASE),
        Category(27, "LOAN INTEREST", 1, R.drawable.wallet_6, R.color.color_27, CategoryType.LOAN_INTEREST),
        Category(28, "DEBT COLLECTION", 1, R.drawable.wallet_11, R.color.color_28, CategoryType.DEBT_COLLECTION),
        Category(29, "LOAN INCREASE", 1, R.drawable.wallet_11, R.color.color_29, CategoryType.LOAN_INCREASE),
        Category(30, "Deposit", 1, R.drawable.deposit, R.color.color_30, CategoryType.DEPOSIT),
        Category(31, "Withdraw", 1, R.drawable.withdraw, R.color.color_31, CategoryType.WITHDRAW),
        Category(32, "PAYABLE", 1, R.drawable.wallet_6, R.color.color_32, CategoryType.PAYABLE),
        Category(33, "RECEIVABLE", 1, R.drawable.wallet_11, R.color.color_33, CategoryType.RECEIVABLE),
        Category(34, "DEBT_INTEREST", 1, R.drawable.wallet_11, R.color.color_34, CategoryType.DEBT_INTEREST),
    )


}