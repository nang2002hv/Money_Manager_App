package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.AccountDao
import com.example.money_manager_app.data.dao.WalletDao
import com.example.money_manager_app.data.model.AccountWithWalletItem
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.toWalletItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AccountRepository {
    fun getAccount(): Flow<List<AccountWithWalletItem>>
    suspend fun getAccountById(accountId: Long): Flow<AccountWithWalletItem>
    suspend fun insertAccount(account: Account): Long
}

@OptIn(ExperimentalCoroutinesApi::class)
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val walletDao: WalletDao
) : AccountRepository {

    override fun getAccount(): Flow<List<AccountWithWalletItem>> {
        return accountDao.getAccount().flatMapConcat { accounts ->
            val accountWithWalletFlows = accounts.map { account ->
                walletDao.getWalletsFullDetailByUserId(account.account.id).map { wallets ->
                    AccountWithWalletItem(account.account, wallets.map { it.toWalletItem() })
                }
            }

            combine(accountWithWalletFlows) { accountWithWalletList ->
                accountWithWalletList.toList()
            }
        }
    }

    override suspend fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account)
    }

    override suspend fun getAccountById(accountId: Long): Flow<AccountWithWalletItem> {
        return accountDao.getAccountById(accountId).flatMapConcat { account ->
            walletDao.getWalletsFullDetailByUserId(account.account.id).map { wallets ->
                AccountWithWalletItem(account.account, wallets.map { it.toWalletItem() })
            }
        }
    }
}
