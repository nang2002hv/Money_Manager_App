package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtDetail
import com.example.money_manager_app.data.model.entity.Transfer
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDebt(debt: Debt) : Long

    @Query("SELECT * FROM debt WHERE account_id = :accountId")
    fun getDebtsByAccountId(accountId: Long): Flow<List<DebtDetail>>

    @Query("SELECT * FROM debt WHERE date = :date AND account_id = :accountId")
    fun getDebtsByDateAndAccountId(date: Long, accountId: Long): Flow<List<Debt>>


    @Query("SELECT * FROM debt WHERE id = :debtId")
    fun getDebtDetailsByDebtId(debtId: Long): Flow<DebtDetail?>

    @Query("SELECT * FROM debt WHERE account_id = :accountId AND wallet_id = :walletId AND date BETWEEN :startDay AND :endDay")
    fun getDebtByWalletAndDayStartAndDayEnd(accountId: Long, walletId: Long, startDay : Long, endDay : Long): List<Debt>

    @Query("SELECT * FROM debt WHERE date BETWEEN :startDay AND :endDay AND account_id = :accountId")
    fun getDebtByDayStartAndDayEnd(accountId: Long, startDay : Long, endDay : Long): Flow<List<Debt>>

    @Query("""
    SELECT * FROM debt
    WHERE (:startDate IS NULL OR date >= :startDate)
    AND (:endDate IS NULL OR date <= :endDate)
    AND (:minAmount IS NULL OR amount >= :minAmount)
    AND (:maxAmount IS NULL OR amount <= :maxAmount)
    AND (:description IS NULL OR description LIKE '%' || :description || '%')
    AND (:categoryType IS NULL OR icon_id = :categoryType)
    AND (:fromWallet IS NULL OR wallet_id = :fromWallet)
    AND account_id = :idAccount
""")
    fun searchByDateAndAmountAndDesAndCategoryAndWallet(
        startDate : Long?,
        endDate : Long?,
        minAmount : Double?,
        maxAmount : Double?,
        description : String?,
        categoryType: Long?,
        fromWallet : Long?,
        idAccount : Long
    ): List<Debt>


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editDebt(debt: Debt)

    @Query("DELETE FROM debt WHERE id = :debtId")
    suspend fun deleteDebt(debtId: Long)

    @Delete
    suspend fun deleteDebt(debt: Debt)

    @Query("SELECT * FROM debt WHERE account_id = :userId")
    fun getDebtListByAccountId(userId: Long): Flow<List<Debt>>

    @Query("SELECT * FROM debt WHERE account_id = :userId AND wallet_id = :walletId")
    fun getDebtListByAccountIdAndWalletId(userId: Long,walletId: Long): Flow<List<Debt>>

    @Query("SELECT * FROM debt WHERE account_id = :userId AND wallet_id = :walletId")
    fun getDebtListByAccountIdAndWallet(userId: Long,walletId: Long): List<Debt>
}