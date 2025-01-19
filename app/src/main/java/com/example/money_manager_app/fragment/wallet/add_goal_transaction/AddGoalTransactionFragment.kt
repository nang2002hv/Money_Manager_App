package com.example.money_manager_app.fragment.wallet.add_goal_transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.databinding.FragmentAddGoalTransactionBinding
import com.example.money_manager_app.fragment.wallet.goal_detail.GoalDetailFragment
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddGoalTransactionFragment :
    BaseFragment<FragmentAddGoalTransactionBinding, AddGoalTransactionViewModel>(R.layout.fragment_add_goal_transaction) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var goalId: Long? = null
    private var goalTransaction: GoalTransaction? = null
    private var inputType: GoalInputType ? = GoalInputType.DEPOSIT
    private val calendar = Calendar.getInstance()

    override fun getVM(): AddGoalTransactionViewModel {
        val vm: AddGoalTransactionViewModel by viewModels()
        return vm
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            goalId = it.getLong("goal")
            goalTransaction = it.getParcelable("goalTransaction")
            inputType = it.getParcelable(GoalDetailFragment.GOAL_ACTION_TYPE)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val goalInputTypeAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            GoalInputType.entries.map { it.name })
        binding.spinnerActionType.adapter = goalInputTypeAdapter
        val walletAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mainViewModel.currentAccount.value?.walletItems?.map { it.wallet.name } ?: emptyList() // Provide an empty list as fallback
        )
        binding.spinnerWallet.adapter = walletAdapter
        binding.spinnerWallet.setSelection(0)

        // Pre-fill fields if editing
        goalTransaction?.let { transaction ->
            binding.apply {
                actionTypeLabel.visibility = View.VISIBLE
                spinnerActionType.visibility = View.VISIBLE
                dateLabel.visibility = View.VISIBLE
                etDate.visibility = View.VISIBLE
                etTime.visibility = View.VISIBLE
            }
            binding.etAmount.setText(getString(R.string.money_amount, "", transaction.amount))
            binding.etDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date)
            binding.etTime.text =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(transaction.time)

            val inputTypeIndex = GoalInputType.entries.indexOf(transaction.type)
            binding.spinnerActionType.setSelection(inputTypeIndex)
            binding.delete.visibility = View.VISIBLE
            val walletName =
                mainViewModel.currentAccount.value!!.walletItems?.find { it.wallet.id == transaction.walletId }!!.wallet.name
            binding.spinnerWallet.setSelection(walletAdapter.getPosition(walletName))
        }

//        binding.etAmount.addTextChangedListener(textWatcher)
//        binding.etDate.addTextChangedListener(textWatcher)
//        binding.etTime.addTextChangedListener(textWatcher)
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initToolbar() {
        super.initToolbar()

        binding.backButton.setOnSafeClickListener {
            onBack()
        }

        binding.saveButton.setOnSafeClickListener {
            // Get amount, date, and time inputs
            val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0

            // Check if date and time are not empty before parsing
            val date = binding.etDate.text.toString().takeIf { it.isNotEmpty() }?.toDateTimestamp() ?: System.currentTimeMillis()
            val time = binding.etTime.text.toString().takeIf { it.isNotEmpty() }?.toTimeTimestamp() ?: System.currentTimeMillis()

            // Find wallet id
            val wallet = mainViewModel.accounts.value.flatMap { it -> it.walletItems ?: emptyList() }
                .find { it.wallet.name == binding.spinnerWallet.selectedItem.toString() }?.wallet?.id ?: return@setOnSafeClickListener

            // Get input type
            val _inputType = GoalInputType.valueOf(binding.spinnerActionType.selectedItem.toString())

            // Create or update goal transaction
            val goalTransaction = goalTransaction?.copy(
                name = "GOAL $_inputType",
                amount = amount,
                date = date,
                time = time,
                walletId = wallet,
                type = _inputType
            ) ?: GoalTransaction(
                name = "GOAL $_inputType",
                amount = amount,
                date = System.currentTimeMillis(),
                time = System.currentTimeMillis(),
                walletId = wallet,
                type = _inputType,
                accountId = mainViewModel.currentAccount.value!!.account.id,
                goalId = goalId ?: 0L
            )

            // Log and validate before saving
            if (goalTransaction.amount <= 0 || goalTransaction.name.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.blank_input), Toast.LENGTH_SHORT).show()
            } else {
                if (goalTransaction.id > 0L) {
                    getVM().editGoalTransaction(goalTransaction)
                } else {
                    getVM().addGoalTransaction(goalTransaction)
                }
                appNavigation.navigateUp()
            }
        }

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.etDate.setOnSafeClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    binding.etDate.text = SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
                    ).format(binding.etDate.text ?: selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.etTime.setOnSafeClickListener {
            val timePicker = TimePickerDialog(
                requireContext(), { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    binding.etTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                        binding.etTime.text ?: selectedTime.time
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        binding.delete.setOnSafeClickListener {
            goalTransaction?.let {
                getVM().deleteGoalTransaction(it.id)
            }
            appNavigation.navigateUp()
        }
    }

//    private val textWatcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            checkSaveButton()
//        }
//
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//    }
//
//    private fun checkSaveButton() {
//        val amount = binding.etAmount.text.toString().toDoubleOrNull()
//        val date = binding.etDate.text.toString()
//        val time = binding.etTime.text.toString()
//        binding.saveButton.isEnabled = amount != null && date.isNotEmpty() && time.isNotEmpty()
//    }
}