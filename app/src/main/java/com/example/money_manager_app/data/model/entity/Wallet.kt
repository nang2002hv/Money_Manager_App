package com.example.money_manager_app.data.model.entity

import android.os.Parcelable
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.WalletItem
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.utils.WalletTypeConverter
import com.example.money_manager_app.utils.calculateCurrentWalletAmount
import kotlinx.parcelize.Parcelize
import kotlin.math.log

@Parcelize
@Entity(
    tableName = "wallet", foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @TypeConverters(WalletTypeConverter::class) @ColumnInfo(name = "wallet_type") val walletType: WalletType,
    @ColumnInfo(name = "name") val name: String,
    @DrawableRes @ColumnInfo(name = "icon_id") val iconId: Int = R.drawable.wallet_14,
    @ColorRes @ColumnInfo(name = "color_id") val colorId: Int = R.color.color_1,
    @ColumnInfo(name = "is_excluded") val isExcluded: Boolean? = null,
    @ColumnInfo(name = "statement_date") val statementDate: Long? = null,
    @ColumnInfo(name = "due_date") val dueDate: Long? = null,
) : Parcelable

@DatabaseView(
    "SELECT * FROM wallet"
)
data class WalletFullDetail(
    @Embedded
    val wallet: Wallet,
    @Relation(
        parentColumn = "id",
        entityColumn = "wallet_id"
    )
    val goalTransactions: List<GoalTransaction>,
    @Relation(
        parentColumn = "id",
        entityColumn = "wallet_id"
    )
    val debts: List<Debt>,
    @Relation(
        parentColumn = "id",
        entityColumn = "wallet_id"
    )
    val debtTransactions: List<DebtTransaction>,
    @Relation(
        parentColumn = "id",
        entityColumn = "from_wallet_id"
    )
    val transferOuts: List<Transfer>,
    @Relation(
        parentColumn = "id",
        entityColumn = "to_wallet_id"
    )
    val transferIns: List<Transfer>,
)

fun WalletFullDetail.toWalletItem(startDate: Long, endDate: Long) : WalletItem {
    val transactions = mutableSetOf<Transaction>()
    transactions.addAll(goalTransactions)
    transactions.addAll(debts)
    transactions.addAll(debtTransactions)
    transactions.addAll(transferIns)
    transactions.addAll(transferOuts)
    Log.d("WalletFullDetail", "toWalletItem: $transactions")
    val dateRangeAmount = transactions.toMutableList().calculateCurrentWalletAmount(wallet.id,startDate,endDate)
    return WalletItem(
        wallet = wallet,
        startingAmount = wallet.amount + dateRangeAmount.startingAmount,
        endingAmount = wallet.amount + dateRangeAmount.endingAmount
    )
}

fun WalletFullDetail.toWalletItem() : WalletItem {
    val transactions = mutableSetOf<Transaction>()
    transactions.addAll(goalTransactions)
    transactions.addAll(debts)
    transactions.addAll(debtTransactions)
    transactions.addAll(transferIns)
    transactions.addAll(transferOuts)
    val dateRangeAmount = transactions.toMutableList().calculateCurrentWalletAmount(wallet.id)
    return WalletItem(
        wallet = wallet,
        startingAmount = wallet.amount + dateRangeAmount.startingAmount,
        endingAmount = wallet.amount + dateRangeAmount.endingAmount
    )
}