package com.example.money_manager_app.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.money_manager_app.R

/**
 * Utility object for managing and providing access to a predefined list of color resources.
 */
object ColorUtils {
    private val colors = R.array.color_array

    /**
     * Retrieves the list of color resource IDs.
     */
    fun getColors(context: Context): List<Int> {
        val typedArray = context.resources.obtainTypedArray(colors)
        return List(typedArray.length()) { typedArray.getResourceId(it, 0) }.also { typedArray.recycle() }
    }

    /**
     * Gets the index of the given color resource ID from the predefined list.
     * Returns -1 if the color is not found in the list.
     *
     * @param context The context to access resources.
     * @param colorId The color resource ID to find.
     * @return The index of the color, or -1 if not found.
     */
    fun getColorIndex(context: Context, colorId: Int): Int {
        val colors = getColors(context)
        return colors.indexOf(colorId)
    }
}
