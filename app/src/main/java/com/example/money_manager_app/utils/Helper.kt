package com.example.money_manager_app.utils

import android.R
import android.content.Context
import android.util.TypedValue
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale



object Helper {

    fun convertDpToPixel(context: Context, dp: Float): Float {
        return dp * (context.resources.displayMetrics.densityDpi / 160.0f)
    }


    fun getAttributeColor(context: Context, resource: Int): Int {
        try {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(resource, typedValue, true)
            return typedValue.data
        } catch (e: Exception) {
            e.printStackTrace()
            return R.color.holo_red_dark
        }
    }


    fun getBeautifyAmount(symbol: String?, amount: Double): String {
        var amount = amount
        val z = 0 > amount
        val sb = StringBuilder()
        sb.append(if (z) "-" else "")
        sb.append(symbol)
        sb.append(StringUtils.SPACE)
        if (z) {
            amount = -amount
        }
        sb.append(getFormattedAmount(amount))
        return sb.toString()
    }


    private fun getFormattedAmount(amount: Double): String {
        val format: String
        if (amount == 0.0) {
            return "0"
        }
        val stripTrailingZeros =
            BigDecimal(amount).divide(BigDecimal(100)).setScale(2, RoundingMode.DOWN)
                .stripTrailingZeros()
        val decimalFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        decimalFormat.maximumFractionDigits = 2
        val i = 5 and 0
        decimalFormat.minimumFractionDigits = 0
        decimalFormat.isGroupingUsed = false
        val format2 = decimalFormat.format(stripTrailingZeros)
        if (!format2.contains(".") && !format2.contains(",")) {
            decimalFormat.isGroupingUsed = true
            format = decimalFormat.format(stripTrailingZeros)
            return format
        }
        format = String.format(Locale.getDefault(), "%,.2f", stripTrailingZeros)
        return format
    }
}