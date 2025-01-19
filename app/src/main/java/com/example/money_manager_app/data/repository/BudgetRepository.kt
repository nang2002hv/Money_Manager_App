package com.example.money_manager_app.data.repository

import android.util.Log
import com.example.money_manager_app.data.dao.BudgetDao
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.BudgetCategoryCrossRef
import com.example.money_manager_app.data.model.entity.BudgetWithCategory
import com.example.money_manager_app.data.model.entity.enums.TransferType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface BudgetRepository {
    suspend fun insertBudget(budget: Budget): Long

    suspend fun editBudget(budget: Budget)

    fun deleteBudget(budget: Budget)

    fun deleteBudget(id: Long)

    fun getAllBudgets(id : Long): List<Budget>

    fun getBudgetsByAccountId(accountId: Long): Flow<List<BudgetWithCategory>>

    fun getBudgetsByAccountIdAndDate(accountId: Long, date : Long): List<BudgetWithCategory>

    suspend fun removeCategoryFromBudget(budgetId: Long, categoryId: Long)

    suspend fun insertBudget(budget: Budget,budgetCategoryCrossRefs : List<BudgetCategoryCrossRef>): Long
    suspend fun editBudget(budget: Budget,budgetCategoryCrossRefs : List<BudgetCategoryCrossRef>)
}

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val tranferRepository: TransferRepository
) : BudgetRepository {
    override suspend fun insertBudget(budget: Budget): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertBudget(budget: Budget,budgetCategoryCrossRefs : List<BudgetCategoryCrossRef>): Long {
        var spent = 0.0
        for(i in budgetCategoryCrossRefs){
            val transferList= tranferRepository.getTransferWithCategoryByAccountId(budget.accountId, i.categoryId, budget.startDate, budget.endDate)
            spent += transferList.filter { it.typeOfExpenditure == TransferType.Expense }.sumOf { it.amount }

        }
        budget.spent = spent.toInt()
        val budgetId = budgetDao.insertBudget(budget)
        for (i in budgetCategoryCrossRefs){
            i.budgetId = budgetId
        }
        budgetDao.insertBudgetCategoryCrossRefs(budgetCategoryCrossRefs)
        return budgetId
    }

    override suspend fun editBudget(budget: Budget) {
        budgetDao.editBudget(budget)
    }

    override fun getAllBudgets(id : Long): List<Budget> {
        return budgetDao.getAllBudgets(id)
    }

    override suspend fun editBudget(budget: Budget,budgetCategoryCrossRefs : List<BudgetCategoryCrossRef>) {
        var spent = 0.0
        for(i in budgetCategoryCrossRefs){
            val transferList= tranferRepository.getTransferWithCategoryByAccountId(budget.accountId, i.categoryId, budget.startDate, budget.endDate)
            spent += transferList.filter { it.typeOfExpenditure == TransferType.Expense }.sumOf { it.amount }

        }
        budget.spent = spent.toInt()
        val budgetId =  budget.id
        for (i in budgetCategoryCrossRefs){
            i.budgetId = budgetId
        }
        budgetDao.deleteBudgetCategoryCrossRefs(budgetId)
        budgetDao.insertBudgetCategoryCrossRefs(budgetCategoryCrossRefs)
        budgetDao.editBudget(budget)
    }

    override fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }

    override fun deleteBudget(id: Long) {
        budgetDao.deleteBudget(id)
    }

    override fun getBudgetsByAccountId(accountId: Long): Flow<List<BudgetWithCategory>> {
        return budgetDao.getBudgetsByAccountId(accountId)
    }

    override fun getBudgetsByAccountIdAndDate(
        accountId: Long,
        date: Long,
    ): List<BudgetWithCategory> {
        return budgetDao.getBudgetsByAccountIdAndDate(accountId, date)
    }

    override suspend fun removeCategoryFromBudget(budgetId: Long, categoryId: Long) {
        budgetDao.deleteBudgetCategoryCrossRef(budgetId, categoryId)
    }

}