package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.GoalTransactionDao
import com.example.money_manager_app.data.model.entity.GoalTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GoalTransactionRepository {
    suspend fun insertGoalTransaction(goalTransaction: GoalTransaction) : Long
    suspend fun updateGoalTransaction(goalTransaction: GoalTransaction) : Int
    suspend fun deleteGoalTransaction(goalTransaction: GoalTransaction)
    suspend fun deleteGoalTransaction(id: Long)
    fun getGoalTransactionsByAccountId(accountId: Long) : Flow<List<GoalTransaction>>
    fun getGoalTransactionsByAccountIdAndWalletId(accountId: Long, walletId: Long) : Flow<List<GoalTransaction>>
}

class GoalTransactionRepositoryImpl @Inject constructor(
    private val goalTransactionDao: GoalTransactionDao
) : GoalTransactionRepository {
    override suspend fun insertGoalTransaction(goalTransaction: GoalTransaction): Long {
        return goalTransactionDao.insertGoalTransaction(goalTransaction)
    }

    override suspend fun updateGoalTransaction(goalTransaction: GoalTransaction): Int {
        return goalTransactionDao.updateGoalTransaction(goalTransaction)
    }

    override suspend fun deleteGoalTransaction(goalTransaction: GoalTransaction) {
        return goalTransactionDao.deleteGoalTransaction(goalTransaction)
    }

    override suspend fun deleteGoalTransaction(id: Long) {
        return goalTransactionDao.deleteGoalTransaction(id)
    }

    override fun getGoalTransactionsByAccountId(accountId: Long): Flow<List<GoalTransaction>> {
        return goalTransactionDao.getGoalTransactionsByAccountId(accountId)
    }

    override fun getGoalTransactionsByAccountIdAndWalletId(accountId: Long, walletId: Long): Flow<List<GoalTransaction>> {
        return goalTransactionDao.getGoalTransactionsByAccountIdAndWalletId(accountId, walletId)
    }
}