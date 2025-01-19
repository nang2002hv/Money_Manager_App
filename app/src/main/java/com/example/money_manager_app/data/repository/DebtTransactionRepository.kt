package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.DebtTransactionDao
import com.example.money_manager_app.data.model.entity.DebtTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DebtTransactionRepository {
    suspend fun insertDebtTransaction(debtTransaction: DebtTransaction): Long
    suspend fun editDebtTransaction(debtTransaction: DebtTransaction)
    fun getDebtTransactionsByDebtId(debtId: Long): Flow<List<DebtTransaction>>
    fun getDebtTransactionsByAccountId(accountId: Long): Flow<List<DebtTransaction>>
    fun getDebtTransactionsByAccountIdAndWalletId(
        accountId: Long,
        walletId: Long
    ): Flow<List<DebtTransaction>>

    fun getDebtTransactionsByAccountIdAndWallet(
        accountId: Long,
        walletId: Long
    ): List<DebtTransaction>

    fun getDebtTransactionFromDayStartAndDayEnd(
        accountId: Long,
        startDay: Long,
        endDay: Long
    ): Flow<List<DebtTransaction>>

    fun getDebtTransactionsByDateAndAccountId(
        date: Long,
        accountId: Long
    ): Flow<List<DebtTransaction>>

    fun searchByDateAndAmountAndDesAndCategoryAndWallet(
        startDate: Long?,
        endDate: Long?,
        minAmount: Double?,
        maxAmount: Double?,
        categoryType: Long?,
        fromWallet: Long?,
        idAccount: Long
    ): List<DebtTransaction>

    fun getDebtTransactionByIdAccountAndIdDebtTrasaction(idAccount: Long, idDebtTransaction: Long) : Flow<DebtTransaction>

    suspend fun deleteDebtTransaction(debtTransactionId: Long)
    suspend fun deleteDebtTransaction(debtTransaction: DebtTransaction)

    fun getDebtTransactionByWalletAndDayStartAndDayEnd(
        accountId: Long,
        walletId: Long,
        startDay: Long,
        endDay: Long
    ): List<DebtTransaction>
}

class DebtTransactionRepositoryImpl @Inject constructor(
    private val debtTransactionDao: DebtTransactionDao
) : DebtTransactionRepository {
    override suspend fun insertDebtTransaction(debtTransaction: DebtTransaction): Long {
        return debtTransactionDao.insertDebtTransaction(debtTransaction)
    }

    override fun getDebtTransactionsByDateAndAccountId(
        date: Long,
        accountId: Long
    ): Flow<List<DebtTransaction>> {
        return debtTransactionDao.getDebtTransactionsByDateAndAccountId(date, accountId)
    }


    override suspend fun editDebtTransaction(debtTransaction: DebtTransaction) {
        debtTransactionDao.editDebtTransaction(debtTransaction)
    }

    override fun searchByDateAndAmountAndDesAndCategoryAndWallet(
        startDate : Long?,
        endDate : Long?,
        minAmount : Double?,
        maxAmount : Double?,
        categoryType: Long?,
        fromWallet : Long?,
        idAccount : Long
    ): List<DebtTransaction> {
        return debtTransactionDao.searchByDateAndAmountAndDesAndCategoryAndWallet(
            startDate,
            endDate,
            minAmount,
            maxAmount,
            categoryType,
            fromWallet,
            idAccount
        )
    }

    override fun getDebtTransactionByIdAccountAndIdDebtTrasaction(
        idAccount: Long,
        idDebtTransaction: Long
    ): Flow<DebtTransaction> {
        return debtTransactionDao.getDebtTransactionByIdAccountAndIdDebtTrasaction(idAccount, idDebtTransaction)
    }

    override fun getDebtTransactionsByDebtId(debtId: Long): Flow<List<DebtTransaction>> {
        return debtTransactionDao.getDebtTransactionsByDebtId(debtId)
    }

    override fun getDebtTransactionsByAccountId(accountId: Long): Flow<List<DebtTransaction>> {
        return debtTransactionDao.getDebtTransactionsByAccountId(accountId)
    }

    override fun getDebtTransactionsByAccountIdAndWalletId(
        accountId: Long,
        walletId: Long
    ): Flow<List<DebtTransaction>> {
        return debtTransactionDao.getDebtTransactionsByAccountIdAndWalletId(accountId, walletId)
    }

    override fun getDebtTransactionsByAccountIdAndWallet(
        accountId: Long,
        walletId: Long
    ): List<DebtTransaction> {
        return debtTransactionDao.getDebtTransactionsByAccountIdAndWallet(accountId, walletId)
    }

    override fun getDebtTransactionFromDayStartAndDayEnd(
        accountId: Long,
        startDay: Long,
        endDay: Long
    ): Flow<List<DebtTransaction>> {
        return debtTransactionDao.getDebtTransactionFromDayStartAndDayEnd(accountId, startDay, endDay)
    }

    override suspend fun deleteDebtTransaction(debtTransactionId: Long) {
        debtTransactionDao.deleteDebtTransaction(debtTransactionId)
    }

    override suspend fun deleteDebtTransaction(debtTransaction: DebtTransaction) {
        debtTransactionDao.deleteDebtTransaction(debtTransaction)
    }

    override fun getDebtTransactionByWalletAndDayStartAndDayEnd(
        accountId: Long,
        walletId: Long,
        startDay: Long,
        endDay: Long
    ): List<DebtTransaction> {
        return debtTransactionDao.getDebtTransactionByWalletAndDayStartAndDayEnd(accountId, walletId, startDay, endDay)
    }

}