package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.GoalDao
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.data.model.entity.GoalDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GoalRepository {
    suspend fun insertGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal): Int
    suspend fun deleteGoal(goal: Goal)
    suspend fun deleteGoalById(id: Long)
    fun getGoalById(id: Long): Flow<GoalDetail?>
    fun getAllGoals(): Flow<List<GoalDetail>>
    fun getGoalsByAccountId(accountId: Long): Flow<List<GoalDetail>>
}

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    override suspend fun insertGoal(goal: Goal): Long {
        return goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: Goal): Int {
        return goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    override suspend fun deleteGoalById(id: Long) {
        goalDao.deleteGoalById(id)
    }

    override fun getGoalById(id: Long): Flow<GoalDetail?> {
        return goalDao.getGoalById(id)
    }

    override fun getAllGoals(): Flow<List<GoalDetail>> {
        return goalDao.getAllGoals()
    }

    override fun getGoalsByAccountId(accountId: Long): Flow<List<GoalDetail>> {
        return goalDao.getGoalsByAccountId(accountId)
    }
}