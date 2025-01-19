package com.example.money_manager_app.data.model.entity

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.SubTransaction
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.utils.GoalInputTypeConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "goal_transaction", foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goal_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Wallet::class,
        parentColumns = ["id"],
        childColumns = ["wallet_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Account::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GoalTransaction(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    @DrawableRes @ColumnInfo(name = "icon_id") override val iconId: Int = R.drawable.wallet_8,
    override val name: String,
    @ColumnInfo(name = "account_id") override val accountId: Long,
    @ColorRes @ColumnInfo(name = "color_id") override val colorId: Int = R.color.color_1,
    @ColumnInfo(name = "goal_id") val goalId: Long,
    @ColumnInfo(name = "wallet_id") override val walletId: Long,
    override val amount: Double,
    @TypeConverters(GoalInputTypeConverter::class) val type: GoalInputType? = GoalInputType.DEPOSIT,
    override val date: Long = System.currentTimeMillis(),
    override val time: Long = System.currentTimeMillis(),
) : SubTransaction(id, iconId, name, amount, colorId, accountId, walletId, date, time),
    Parcelable