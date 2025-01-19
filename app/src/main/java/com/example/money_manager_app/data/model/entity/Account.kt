package com.example.money_manager_app.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.example.money_manager_app.data.model.entity.enums.Currency
import com.example.money_manager_app.utils.CurrencyTypeConverter

@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name_account") val nameAccount: String,
    @TypeConverters(CurrencyTypeConverter::class) val currency: Currency
)

data class AccountWithWallet(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "id",
        entityColumn = "account_id"
    )
    val wallets: List<Wallet>?
)