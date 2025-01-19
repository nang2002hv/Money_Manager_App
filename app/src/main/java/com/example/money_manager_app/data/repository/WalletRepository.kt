package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.WalletDao
import com.example.money_manager_app.data.model.WalletItem
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.toWalletItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface WalletRepository {
    fun getWalletsByUserId(userId: Long): Flow<List<Wallet>>
    fun getWalletById(walletId: Long): Flow<Wallet>
    suspend fun insertWallet(wallet: Wallet): Long
    suspend fun editWallet(wallet: Wallet)
    suspend fun deleteWallet(walletId: Long)
    suspend fun deleteWallet(wallet: Wallet)
    fun getWalletItemsByUserId(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<WalletItem>>
    fun getWalletItemsByUserId(userId: Long): Flow<List<WalletItem>>
}

class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao
) : WalletRepository {
    override fun getWalletsByUserId(userId: Long): Flow<List<Wallet>> {
        return walletDao.getWalletsByUserId(userId)
    }

    override fun getWalletById(walletId: Long): Flow<Wallet> {
        return walletDao.getWalletById(walletId)
    }

    override suspend fun insertWallet(wallet: Wallet): Long {
        return walletDao.insertWallet(wallet)
    }

    override suspend fun editWallet(wallet: Wallet) {
        walletDao.editWallet(wallet)
    }

    override suspend fun deleteWallet(walletId: Long) {
        walletDao.deleteWallet(walletId)
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        walletDao.deleteWallet(wallet)
    }

    override fun getWalletItemsByUserId(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<WalletItem>> {
        return walletDao.getWalletsFullDetailByUserId(userId)
            .map { wallets ->
                wallets.map { it.toWalletItem(startDate, endDate) }
            }
    }

    override fun getWalletItemsByUserId(userId: Long): Flow<List<WalletItem>> {
        return walletDao.getWalletsFullDetailByUserId(userId)
            .map { wallets ->
                wallets.map { it.toWalletItem() }
            }
    }
}
