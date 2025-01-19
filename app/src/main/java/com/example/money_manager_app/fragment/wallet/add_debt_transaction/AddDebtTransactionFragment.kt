package com.example.money_manager_app.fragment.wallet.add_debt_transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.databinding.FragmentAddDebtTransactionBinding
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddDebtTransactionFragment :
    BaseFragment<FragmentAddDebtTransactionBinding, AddDebtTransactionViewModel>(R.layout.fragment_add_debt_transaction) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var debt: Debt? = null
    private var debTransaction: DebtTransaction? = null
    private val calendar = Calendar.getInstance()

    override fun getVM(): AddDebtTransactionViewModel {
        val vm: AddDebtTransactionViewModel by viewModels()
        return vm
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            debt = it.getParcelable("debt")
            debTransaction = it.getParcelable("debtTransaction")
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val payableActions =
            DebtActionType.entries.filter { it != DebtActionType.DEBT_COLLECTION && it != DebtActionType.LOAN_INCREASE && it != DebtActionType.LOAN_INTEREST }
                .map { it.name }
        val receivableActions =
            DebtActionType.entries.filter { it != DebtActionType.DEBT_INCREASE && it != DebtActionType.REPAYMENT && it != DebtActionType.DEBT_INTEREST }
                .map { it.name }

        val debtAction = if(debt!!.type == DebtType.PAYABLE) {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, payableActions)
        } else {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, receivableActions)
        }
        binding.spinnerActionType.adapter = debtAction
        val walletAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            mainViewModel.currentAccount.value!!.walletItems.map { it.wallet.name })
        binding.spinnerWallet.adapter = walletAdapter
        binding.spinnerWallet.setSelection(0)

        // Pre-fill fields if editing
        debTransaction?.let { transaction ->
            binding.etName.setText(transaction.name)
            binding.etAmount.setText(getString(R.string.money_amount, "", transaction.amount))
            binding.etDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date)
            binding.etTime.text =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(transaction.time)

            val actionTypeIndex = DebtActionType.entries.indexOf(transaction.action)
            binding.spinnerActionType.setSelection(actionTypeIndex)
            val walletName =
                mainViewModel.currentAccount.value!!.walletItems.find { it.wallet.id == transaction.walletId }!!.wallet.name
            binding.spinnerWallet.setSelection(walletAdapter.getPosition(walletName))
            binding.delete.visibility = View.VISIBLE
        }

        // Default setup for date and time fields
        if (debTransaction == null) {
            binding.etDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            binding.etTime.text =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }

        binding.etName.addTextChangedListener(textWatcher)
        binding.etAmount.addTextChangedListener(textWatcher)
        binding.etDate.addTextChangedListener(textWatcher)
        binding.etTime.addTextChangedListener(textWatcher)
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initToolbar() {
        super.initToolbar()

        binding.backButton.setOnClickListener {
            onBack()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.etName.text.toString()
            val amount = binding.etAmount.text.toString().toDouble()
            val date = binding.etDate.text.toString().toDateTimestamp()
            val time = binding.etTime.text.toString().toTimeTimestamp()
            val wallet = mainViewModel.accounts.value.flatMap { it.walletItems }
                .find { it.wallet.name == binding.spinnerWallet.selectedItem.toString() }!!.wallet.id
            val actionType =
                DebtActionType.valueOf(binding.spinnerActionType.selectedItem.toString())

            // Create or update transaction
            val debtTransaction = debTransaction?.copy(
                name = name,
                amount = amount,
                date = date,
                time = time,
                walletId = wallet,
                action = actionType
            ) ?: DebtTransaction(
                name = name,
                amount = amount,
                date = date,
                time = time,
                walletId = wallet,
                action = actionType,
                accountId = mainViewModel.currentAccount.value!!.account.id,
                debtId = debt!!.id
            )


            if (debTransaction == null) {
                getVM().addDebtTransaction(debtTransaction)
            } else {
                getVM().updateDebtTransaction(debtTransaction)
            }

            appNavigation.navigateUp()
        }
    }


    override fun setOnClick() {
        super.setOnClick()

        binding.etDate.setOnSafeClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etDate.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.etTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.etTime.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        binding.delete.setOnSafeClickListener {
            debTransaction?.let {
                getVM().deleteDebtTransaction(it.id)
            }
            appNavigation.navigateUp()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkSaveButton()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkSaveButton() {
        val name = binding.etName.text.toString()
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val date = binding.etDate.text.toString()
        val time = binding.etTime.text.toString()
        binding.saveButton.isEnabled =
            amount != null && name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()
    }
}