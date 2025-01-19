package com.example.money_manager_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.money_manager_app.data.dao.AccountDao
import com.example.money_manager_app.data.dao.BudgetDao
import com.example.money_manager_app.data.dao.CategoryDao
import com.example.money_manager_app.data.dao.DebtDao
import com.example.money_manager_app.data.dao.DebtTransactionDao
import com.example.money_manager_app.data.dao.GoalDao
import com.example.money_manager_app.data.dao.GoalTransactionDao
import com.example.money_manager_app.data.dao.TransferDao
import com.example.money_manager_app.data.dao.WalletDao
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.BudgetCategoryCrossRef
import com.example.money_manager_app.data.model.entity.Wallet

@Database(
    entities = [
        Account::class, Budget::class, Goal::class, Category::class, BudgetCategoryCrossRef::class,
        Transfer::class, Wallet::class, Debt::class, DebtTransaction::class, GoalTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun debtTransactionDao(): DebtTransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun goalTransactionDao(): GoalTransactionDao
    abstract fun transferDao(): TransferDao
    abstract fun walletDao(): WalletDao
}