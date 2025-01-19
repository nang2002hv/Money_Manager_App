package com.example.money_manager_app.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import com.example.money_manager_app.R

class ColorSpinnerAdapter(
    context: Context,
    private val colors: List<Int>
) : ArrayAdapter<Int>(context, 0, colors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createColorView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createColorView(position, convertView, parent)
    }

    private fun createColorView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item_color, parent, false)

        val colorView: FrameLayout = view.findViewById(R.id.colorView)
        colorView.background = ColorDrawable(context.getColor(colors[position]))

        return view
    }
}
