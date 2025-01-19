    package com.example.money_manager_app.data.repository

    import android.util.Log
    import androidx.room.Query
    import com.example.money_manager_app.data.dao.TransferDao
    import com.example.money_manager_app.data.model.entity.Category
    import com.example.money_manager_app.data.model.entity.DebtTransaction
    import com.example.money_manager_app.data.model.entity.Transfer
    import kotlinx.coroutines.flow.Flow
    import javax.inject.Inject

    interface TransferRepository {
        suspend fun insertTransferDetail(transfer: Transfer): Long
        suspend fun editTransferDetail(transfer: Transfer)
        fun getTransferFromDayStartAndDayEnd(
            startDay: Long,
            endDay: Long,
            accountId: Long
        ): Flow<List<Transfer>>
        fun getTransferByAccountId(accountId: Long): Flow<List<Transfer>>
        fun searchByDateAndAmountAndDesAndCategoryAndWallet(
            startDate: Long?,
            endDate: Long?,
            minAmount: Double?,
            maxAmount: Double?,
            description: String?,
            fromWallet: Long?,
            categoryId: Long?,
            idAccount : Long
        ): List<Transfer>

        fun getTransfer(accountId: Long, idTransfer: Long): Flow<Transfer>

        fun getAllTransfer(date: Long): Flow<List<Transfer>>

        fun getTransferWithCategoryByAccountId(accountId: Long, categoryId: Long, dateStart : Long, dateEnd : Long): List<Transfer>

        suspend fun deleteTransfer(transferId: Long)

        fun getTransferWithCategoryByAccountIdAndWalletId(accountId: Long, walletId: Long): Flow<List<Transfer>>

        fun getTransferByWalletAndDayStartAndDayEnd(accountId: Long, walletId: Long, startDay : Long, endDay : Long): List<Transfer>

        fun getTransferByWalletAndDayStartAndDayEnd(accountId: Long, walletId: Long): List<Transfer>

    }


    class TransferRepositoryImpl @Inject constructor(
        private val transferDao: TransferDao
    ) : TransferRepository {

        override suspend fun insertTransferDetail(transfer: Transfer): Long {
            return transferDao.insertTransfer(transfer)
        }

        override suspend fun editTransferDetail(transfer: Transfer) {
            transferDao.editTransfer(transfer)
        }

        override fun getTransferFromDayStartAndDayEnd(
            startDay: Long,
            endDay: Long,
            accountId: Long
        ): Flow<List<Transfer>> {
            return transferDao.getTransferFromDayStartAndDayEnd(startDay, endDay, accountId)
        }

        override fun getTransferByAccountId(accountId: Long): Flow<List<Transfer>> {
            return transferDao.getTransfersByAccountId(accountId)
        }

        override fun searchByDateAndAmountAndDesAndCategoryAndWallet(
            startDate: Long?,
            endDate: Long?,
            minAmount: Double?,
            maxAmount: Double?,
            description: String?,
            fromWallet: Long?,
            categoryId: Long?,
            idAccount: Long
        ): List<Transfer> {
            return transferDao.searchByDateAndAmountAndDesAndCategoryAndWallet(
                startDate, endDate, minAmount, maxAmount, description, fromWallet,categoryId, idAccount
            )
        }

        override fun getTransfer(accountId: Long, idTransfer: Long): Flow<Transfer> {
            return transferDao.getTransfer(accountId, idTransfer)
        }


        override fun getAllTransfer(date: Long): Flow<List<Transfer>> {
            return transferDao.getAllTransfer(date)
        }

        override fun getTransferWithCategoryByAccountId(accountId: Long, categoryId: Long, dateStart: Long, dateEnd: Long): List<Transfer> {
            return transferDao.getTransferWithCategoryByAccountId(accountId, categoryId, dateStart, dateEnd)
        }

        override suspend fun deleteTransfer(transferId: Long) {
            transferDao.deleteTransfer(transferId)
        }

        override fun getTransferWithCategoryByAccountIdAndWalletId(
            accountId: Long,
            walletId: Long
        ): Flow<List<Transfer>> {
            return transferDao.getTransferWithCategoryByAccountIdAndWalletId(accountId, walletId)
        }

        override fun getTransferByWalletAndDayStartAndDayEnd(
            accountId: Long,
            walletId: Long,
            startDay: Long,
            endDay: Long
        ): List<Transfer> {
            return transferDao.getTransferByWalletAndDayStartAndDayEnd(accountId, walletId, startDay, endDay)
        }

        override fun getTransferByWalletAndDayStartAndDayEnd(
            accountId: Long,
            walletId: Long
        ): List<Transfer> {
            return transferDao.getTransferByWalletAndDayStartAndDayEnd(accountId, walletId)
        }
    }
