package com.example.money_manager_app.utils

import androidx.room.TypeConverter
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.data.model.entity.enums.Currency
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.PeriodType
import com.example.money_manager_app.data.model.entity.enums.WalletType
import java.sql.Time
import java.util.Calendar
import java.util.Date

class WalletTypeConverter {

    @TypeConverter
    fun fromWalletType(walletType: WalletType): String {
        return walletType.name
    }

    @TypeConverter
    fun toWalletType(value: String): WalletType {
        return WalletType.valueOf(value)
    }
}

class CurrencyTypeConverter {
    @TypeConverter
    fun fromCurrency(currency: Currency): Int {
        return currency.id
    }

    @TypeConverter
    fun toCurrency(id: Int): Currency? {
        return Currency.fromId(id)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTime(value: Long?): Time? {
        return value?.let { Time(it) }
    }

    @TypeConverter
    fun timeToTimestamp(time: Time?): Long? {
        return time?.time
    }

    @TypeConverter
    fun getDayOfWeekString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Chủ Nhật"
            Calendar.MONDAY -> "Thứ Hai"
            Calendar.TUESDAY -> "Thứ Ba"
            Calendar.WEDNESDAY -> "Thứ Tư"
            Calendar.THURSDAY -> "Thứ Năm"
            Calendar.FRIDAY -> "Thứ Sáu"
            Calendar.SATURDAY -> "Thứ Bảy"
            else -> "Không xác định"
        }
    }

    @TypeConverter
    fun transfersDay(day: String, month: String, year: String) : Long {
        var ngay : String = "$day/$month/$year"
        val date : Long = ngay.toDateTimestamp()
        return date
    }
}

class GoalInputTypeConverter {
    @TypeConverter
    fun fromGoalInputType(goalInputType: GoalInputType): String {
        return goalInputType.name
    }

    @TypeConverter
    fun toGoalInputType(value: String): GoalInputType {
        return GoalInputType.valueOf(value)
    }
}

class DebtActionTypeConverter {
    @TypeConverter
    fun fromDebtType(debtActionType: DebtActionType): String {
        return debtActionType.name
    }

    @TypeConverter
    fun toDebtType(value: String): DebtActionType {
        return DebtActionType.valueOf(value)
    }
}

class DebtTypeConverter {
    @TypeConverter
    fun fromDebtType(debtActionType: DebtActionType): String {
        return debtActionType.name
    }

    @TypeConverter
    fun toDebtType(value: String): DebtActionType {
        return DebtActionType.valueOf(value)
    }
}

class PeriodTypeConverter {
    @TypeConverter
    fun fromPeriodType(periodType: PeriodType): String {
        return periodType.name
    }

    @TypeConverter
    fun toPeriodType(value: String): PeriodType {
        return PeriodType.valueOf(value)
    }
}

class CategoryTypeConverter {
    @TypeConverter
    fun fromCategoryType(categoryType: CategoryType): String {
        return categoryType.name
    }

    @TypeConverter
    fun toCategoryType(value: String): CategoryType {
        return CategoryType.valueOf(value)
    }
}

