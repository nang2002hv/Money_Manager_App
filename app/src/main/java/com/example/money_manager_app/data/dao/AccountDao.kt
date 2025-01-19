package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.AccountWithWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAccount() : Flow<List<AccountWithWallet>>

    @Query("SELECT * FROM account WHERE id = :accountId")
    fun getAccountById(accountId: Long) : Flow<AccountWithWallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account) : Long
}