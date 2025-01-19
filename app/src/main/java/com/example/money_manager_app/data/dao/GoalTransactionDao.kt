package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.money_manager_app.data.model.entity.GoalTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalTransaction(goalTransaction: GoalTransaction) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoalTransaction(goalTransaction: GoalTransaction) : Int

    @Delete
    suspend fun deleteGoalTransaction(goalTransaction: GoalTransaction)

    @Query("DELETE FROM goal_transaction WHERE id = :id")
    suspend fun deleteGoalTransaction(id: Long)

    @Query("SELECT * FROM goal_transaction WHERE account_id = :accountId")
    fun getGoalTransactionsByAccountId(accountId: Long) : Flow<List<GoalTransaction>>

    @Query("SELECT * FROM goal_transaction WHERE account_id = :accountId AND wallet_id = :walletId")
    fun getGoalTransactionsByAccountIdAndWalletId(accountId: Long, walletId: Long) : Flow<List<GoalTransaction>>
}