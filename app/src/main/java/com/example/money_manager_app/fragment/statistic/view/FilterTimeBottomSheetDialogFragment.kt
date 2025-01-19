package com.example.money_manager_app.fragment.statistic.view

import android.os.Bundle
import android.view.View
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseBottomSheet
import com.example.money_manager_app.databinding.SheetDialogFragmentBinding
import com.example.money_manager_app.fragment.statistic.adapter.StaticInterface
import com.example.money_manager_app.utils.TimeType

class FilterTimeBottomSheetDialogFragment : BaseBottomSheet<SheetDialogFragmentBinding>() {

    private var staticInterface : StaticInterface? = null

    override fun getLayoutId(): Int {
        return R.layout.sheet_dialog_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectTime()
    }

    private fun selectTime(){
        binding.constraintLayoutall.setOnClickListener {
            staticInterface?.onClickTime(TimeType.ALL)
            dismiss()
        }
        binding.constraintLayoutdaily.setOnClickListener {
            staticInterface?.onClickTime(TimeType.DAILY)
            dismiss()
        }
        binding.constraintLayoutweekly.setOnClickListener {
            staticInterface?.onClickTime(TimeType.WEEKLY)
            dismiss()
        }
        binding.constraintLayoutmonthly.setOnClickListener {
            staticInterface?.onClickTime(TimeType.MONTHLY)
            dismiss()
        }
        binding.constraintLayoutyearly.setOnClickListener {
            staticInterface?.onClickTime(TimeType.YEARLY)
            dismiss()
        }

        binding.constraintLayoutcustom.setOnClickListener {
            val bottomSheet = SelectDateBottomSheet()
            bottomSheet.setStaticInterfacce(staticInterface!!)
            bottomSheet.show(childFragmentManager, "SelectDateBottomSheet")
        }


    }

    fun setStaticInterfacce(staticInterface: StaticInterface) {
        this.staticInterface = staticInterface
    }
}
