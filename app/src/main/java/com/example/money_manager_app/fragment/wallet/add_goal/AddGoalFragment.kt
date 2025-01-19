package com.example.money_manager_app.fragment.wallet.add_goal

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.ColorSpinnerAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.databinding.FragmentAddGoalBinding
import com.example.money_manager_app.utils.ColorUtils
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toFormattedDateString
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddGoalFragment :
    BaseFragment<FragmentAddGoalBinding, AddGoalViewModel>(R.layout.fragment_add_goal) {

    private var goal: Goal? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var colorAdapter: ColorSpinnerAdapter

    override fun getVM(): AddGoalViewModel {
        val viewModel: AddGoalViewModel by viewModels()
        return viewModel
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            goal = it.getParcelable("goal")
            goal?.let {
                getVM().setName(it.name)
                getVM().setAmount(it.amount)
                getVM().setTargetDate(it.targetDate)
            }
            getVM().checkSavable()
        }
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initToolbar() {
        super.initToolbar()

        binding.backArrowButton.setOnSafeClickListener {
            onBack()
        }

        binding.saveButton.setOnSafeClickListener {
            val targetDate = binding.dateTextView.text.toString().toDateTimestamp()
            val colorId =
                colorAdapter.getItem(binding.colorSpinner.selectedItemPosition) ?: R.color.color_1
            goal?.let {
                if(it.amount <= 0 || it.name.isEmpty()){
                    Toast.makeText(requireActivity(),getString(R.string.blank_input),
                        Toast.LENGTH_SHORT).show()
                }else{
                    getVM().saveGoal(it.id, it.accountId, targetDate, colorId)
                    appNavigation.navigateUp()
                }
            } ?: run {
                getVM().saveGoal(
                    null, mainViewModel.currentAccount.value!!.account.id, targetDate, colorId
                )
                appNavigation.navigateUp()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val currencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)

        binding.apply {
            colorAdapter = ColorSpinnerAdapter(requireContext(), ColorUtils.getColors(requireContext()))
            colorSpinner.adapter = colorAdapter
            goal?.let {
                titleText.text = getString(R.string.edit_goal)
                editTextName.setText(it.name)
                editTextAmount.hint = getString(R.string.money_amount, currencySymbol, it.amount)
                editTextAmount.setText(getString(R.string.amount_number, it.amount))
                dateTextView.text = it.targetDate.toFormattedDateString()
                val colorIndex = ColorUtils.getColors(requireContext()).indexOf(it.colorId)
                colorSpinner.setSelection(colorIndex)
            } ?: run {
                titleText.text = getString(R.string.add_goal)
                editTextAmount.hint = getString(R.string.money_amount, currencySymbol, 0.0)
                dateTextView.text = SimpleDateFormat(
                    "dd/MM/yyyy", Locale.getDefault()
                ).format(Calendar.getInstance().time)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getVM().setName(s.toString())
                getVM().checkSavable()
            }

            override fun afterTextChanged(s: android.text.Editable?) {
            }
        })

        binding.editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getVM().setAmount(s.toString().toDoubleOrNull() ?: 0.0)
                getVM().checkSavable()
            }

            override fun afterTextChanged(s: android.text.Editable?) {
            }
        })

        getVM().isSavable.observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = it
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        val calendar = Calendar.getInstance()
        binding.dateTextView.setOnSafeClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.dateTextView.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

}