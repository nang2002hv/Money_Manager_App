package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.WalletFullDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWallet(wallet: Wallet) : Long

    @Query("SELECT * FROM wallet where account_id = :userId")
    fun getWalletsByUserId(userId: Long) : Flow<List<Wallet>>

    @Query("SELECT * FROM wallet WHERE id = :walletId")
    fun getWalletById(walletId: Long) : Flow<Wallet>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editWallet(wallet: Wallet)

    @Query("DELETE FROM wallet WHERE id = :walletId")
    suspend fun deleteWallet(walletId: Long)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    @Query("SELECT * FROM wallet where account_id = :userId")
    fun getWalletsFullDetailByUserId(userId: Long) : Flow<List<WalletFullDetail>>
}