package com.example.money_manager_app.data.model.entity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.utils.CategoryTypeConverter

@Entity(
    tableName = "category", foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id") val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @DrawableRes @ColumnInfo(name = "icon_id") val iconId: Int,
    @ColorRes @ColumnInfo(name = "color_id") val colorId: Int,
    @TypeConverters(CategoryTypeConverter::class)
    @ColumnInfo(name = "category_type") val type: CategoryType
)


data class CategoryWithTransfer(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "transfer_id"
    )
    val transfers: List<Transfer>
)