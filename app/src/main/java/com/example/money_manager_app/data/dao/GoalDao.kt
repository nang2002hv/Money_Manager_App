package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.data.model.entity.GoalDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoal(goal: Goal) : Int

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("DELETE FROM goal WHERE id = :id")
    suspend fun deleteGoalById(id: Long)

    @Query("SELECT * FROM goal WHERE id = :id")
    fun getGoalById(id: Long) : Flow<GoalDetail?>

    @Query("SELECT * FROM goal WHERE account_id = :accountId")
    fun getGoalsByAccountId(accountId: Long) : Flow<List<GoalDetail>>

    @Query("SELECT * FROM goal")
    fun getAllGoals() : Flow<List<GoalDetail>>
}