package com.example.money_manager_app.fragment.statistic.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseBottomSheet
import com.example.money_manager_app.databinding.SheetDateTimeBinding
import com.example.money_manager_app.fragment.statistic.adapter.StaticInterface
import com.example.money_manager_app.utils.toDateTimestamp
import java.util.Calendar
import java.util.Date

class SelectDateBottomSheet : BaseBottomSheet<SheetDateTimeBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.sheet_date_time
    }

    private var staticInterface : StaticInterface? = null

    fun setStaticInterfacce(staticInterface: StaticInterface) {
        this.staticInterface = staticInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.startDate.text = selectedDate
            }
        }

        binding.enddate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.enddate.text = selectedDate
            }
        }

        binding.cancelFilterButton.setOnClickListener {
            dismiss()
        }

        binding.applyFilterButton.setOnClickListener {
            val startDate = binding.startDate.text.toString().toDateTimestamp()
            val endDate = binding.enddate.text.toString().toDateTimestamp()
            staticInterface?.onClickTime(Date(startDate), Date(endDate))
            dismiss()
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }
}