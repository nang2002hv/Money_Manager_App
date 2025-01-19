package com.example.money_manager_app.data.repository

import androidx.room.Query
import com.example.money_manager_app.data.dao.DebtDao
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DebtRepository {
    fun getDebtsByAccountId(userId: Long): Flow<List<DebtDetail>>
    fun getDebtListByAccountId(userId: Long): Flow<List<Debt>>
    fun getDebtDetailsByDebtId(debtId: Long): Flow<DebtDetail?>
    fun getDebtsByDateAndAccountId(date: Long, accountId: Long): Flow<List<Debt>>
    fun getDebtByDayStartAndDayEnd(accountId: Long, startDay: Long, endDay: Long): Flow<List<Debt>>
    fun getDebtByWalletAndDayStartAndDayEnd(accountId: Long, walletId: Long, startDay : Long, endDay : Long): List<Debt>
    fun searchByDateAndAmountAndDesAndCategoryAndWallet(
        startDate: Long?,
        endDate: Long?,
        minAmount: Double?,
        maxAmount: Double?,
        description: String?,
        categoryType: Long?,
        fromWallet: Long?,
        idAccount: Long
    ): List<Debt>

    suspend fun insertDebt(debt: Debt): Long
    suspend fun editDebt(debt: Debt)
    suspend fun deleteDebt(debtId: Long)
    suspend fun deleteDebt(debt: Debt)
    fun getDebtListByAccountIdAndWalletId(userId: Long, walletId: Long): Flow<List<Debt>>
    fun getDebtListByAccountIdAndWallet(userId: Long, walletId: Long): List<Debt>
}

class DebtRepositoryImpl @Inject constructor(
    private val debtDao: DebtDao
) : DebtRepository {
    override fun getDebtsByAccountId(userId: Long): Flow<List<DebtDetail>> {
        return debtDao.getDebtsByAccountId(userId)
    }

    override fun getDebtListByAccountId(userId: Long): Flow<List<Debt>> {
        return debtDao.getDebtListByAccountId(userId)
    }

    override fun getDebtDetailsByDebtId(debtId: Long): Flow<DebtDetail?> {
        return debtDao.getDebtDetailsByDebtId(debtId)
    }

    override fun getDebtsByDateAndAccountId(date: Long, accountId: Long): Flow<List<Debt>> {
        return debtDao.getDebtsByDateAndAccountId(date, accountId)
    }

    override fun searchByDateAndAmountAndDesAndCategoryAndWallet(
        startDate: Long?,
        endDate: Long?,
        minAmount: Double?,
        maxAmount: Double?,
        description: String?,
        categoryType: Long?,
        fromWallet: Long?,
        idAccount: Long
    ): List<Debt> {
        return debtDao.searchByDateAndAmountAndDesAndCategoryAndWallet(
            startDate, endDate, minAmount, maxAmount, description, categoryType, fromWallet, idAccount
        )
    }


    override fun getDebtByDayStartAndDayEnd(
        accountId: Long, startDay: Long, endDay: Long
    ): Flow<List<Debt>> {
        return debtDao.getDebtByDayStartAndDayEnd(accountId, startDay, endDay)
    }

    override fun getDebtByWalletAndDayStartAndDayEnd(
        accountId: Long,
        walletId: Long,
        startDay: Long,
        endDay: Long
    ): List<Debt> {
        return debtDao.getDebtByWalletAndDayStartAndDayEnd(accountId, walletId, startDay, endDay)
    }

    override suspend fun insertDebt(debt: Debt): Long {
        return debtDao.insertDebt(debt)
    }

    override suspend fun editDebt(debt: Debt) {
        debtDao.editDebt(debt)
    }

    override suspend fun deleteDebt(debtId: Long) {
        debtDao.deleteDebt(debtId)
    }

    override suspend fun deleteDebt(debt: Debt) {
        debtDao.deleteDebt(debt)
    }

    override fun getDebtListByAccountIdAndWalletId(
        userId: Long,
        walletId: Long
    ): Flow<List<Debt>> {
        return debtDao.getDebtListByAccountIdAndWalletId(userId, walletId)
    }

    override fun getDebtListByAccountIdAndWallet(userId: Long, walletId: Long): List<Debt> {
        return debtDao.getDebtListByAccountIdAndWallet(userId, walletId)
    }
}