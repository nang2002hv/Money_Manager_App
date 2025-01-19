package com.example.money_manager_app.fragment.wallet.add_debt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.ColorSpinnerAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.databinding.FragmentAddDebtBinding
import com.example.money_manager_app.utils.ColorUtils
import com.example.money_manager_app.utils.getCurrencySymbol
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class AddDebtFragment :
    BaseFragment<FragmentAddDebtBinding, AddDebtViewModel>(R.layout.fragment_add_debt) {

    private var debt: Debt? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var gestureDetector: GestureDetector
    override fun getVM(): AddDebtViewModel {
        val vm: AddDebtViewModel by viewModels()
        return vm
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            debt = it.getParcelable("debt")
            debt?.let { getVM().getDebtDetails(it.id) }
        }
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initToolbar() {
        super.initToolbar()
        binding.backArrowButton.setOnClickListener {
            onBack()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val calendar = Calendar.getInstance()
        binding.dateTextView.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        binding.timeTextView.text =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

        binding.dateTextView.setOnSafeClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.dateTextView.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                    checkFieldsForEmptyValues()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.timeTextView.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.timeTextView.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                    checkFieldsForEmptyValues()
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        val colorAdapter = ColorSpinnerAdapter(requireContext(), ColorUtils.getColors(requireContext()))
        binding.colorSpinner.adapter = colorAdapter
        binding.colorSpinner.setSelection(0)

        // Add TextWatchers to EditTexts
        binding.editTextName.addTextChangedListener(textWatcher)
        binding.editTextDescription.addTextChangedListener(textWatcher)
        binding.editTextAmount.addTextChangedListener(textWatcher)
        binding.editTextAmount.hint = getString(
            R.string.money_amount,
            getCurrencySymbol(
                requireContext(),
                mainViewModel.currentAccount.value!!.account.currency
            ),
            0.0
        )

        if (debt == null) {
            getVM().initDebtTypeIndex(0)
        }

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y
                val velocityThreshold = 1000 // Minimum velocity for a swipe
                val distanceThreshold = 150 // Minimum distance for a swipe


                // Check if it's a horizontal swipe
                if (abs(deltaX) > abs(deltaY) && abs(deltaX) > distanceThreshold && abs(velocityX) > velocityThreshold) {
                    if (deltaX > 0) {
                        // Swipe right
                        getVM().toggleCurrentDebtTypeIndex()
                    } else {
                        // Swipe left
                        getVM().toggleCurrentDebtTypeIndex()
                    }
                    return true
                }
                return false
            }
        })

        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

    }

    override fun bindingStateView() {
        super.bindingStateView()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.accounts.collect { accounts ->
                    val wallets = accounts.flatMap { it.walletItems }
                    val walletNames = wallets.map { it.wallet.name }
                    val walletAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_item, walletNames
                    )
                    binding.walletSpinner.adapter = walletAdapter
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().debt.collect { debt ->
                    if (debt != null) {
                        populateFieldsWithDebtData(debt.debt)
                        binding.saveButton.setOnSafeClickListener {
                            val updatedDebt = buildDebtFromFields(debt.debt.id)
                            if(updatedDebt.amount <= 0 || updatedDebt.name.isEmpty()){
                                Toast.makeText(requireActivity(),getString(R.string.blank_input),
                                    Toast.LENGTH_SHORT).show()
                            }else{
                                getVM().editDebt(updatedDebt)
                                appNavigation.navigateUp()
                            }
                        }
                    } else {
                        binding.saveButton.setOnSafeClickListener {
                            val newDebt = buildDebtFromFields()
                            if(newDebt.amount < 0 || newDebt.name.isEmpty()){
                                Toast.makeText(requireActivity(),getString(R.string.blank_input),
                                    Toast.LENGTH_SHORT).show()
                            }else{
                                getVM().addDebt(newDebt)
                                appNavigation.navigateUp()
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            getVM().currentDebtTypeIndex.observe(viewLifecycleOwner) {
                binding.debtTypeTextView.text = DebtType.entries[it].name
                binding.editTextName.hint = if(it == 0) getString(R.string.who_do_you_borrow_to)
                    else getString(R.string.who_do_you_lend_from)
            }
        }

    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkFieldsForEmptyValues()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkFieldsForEmptyValues() {
        val name = binding.editTextName.text.toString()
        val description = binding.editTextDescription.text.toString()
        val amount = binding.editTextAmount.text.toString()
        val date = binding.dateTextView.text.toString()
        val time = binding.timeTextView.text.toString()

        binding.saveButton.isEnabled =
            name.isNotEmpty() && description.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()
    }

    private fun populateFieldsWithDebtData(debt: Debt) {
        binding.editTextName.setText(debt.name)
        binding.editTextDescription.setText(debt.description)
        binding.editTextAmount.setText(getString(R.string.money_amount, "", debt.amount))
        binding.dateTextView.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(debt.date)
        binding.timeTextView.text =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(debt.time)
        binding.walletSpinner.setSelection(getWalletIndex(debt.walletId))

        val colorIndex = ColorUtils.getColorIndex(requireContext(), debt.colorId)
        binding.colorSpinner.setSelection(colorIndex)
    }


    private fun getWalletIndex(walletId: Long): Int {
        val wallets = mainViewModel.currentAccount.value?.walletItems.orEmpty()
        return wallets.indexOfFirst { it.wallet.id == walletId }.takeIf { it >= 0 } ?: 0
    }

    private fun buildDebtFromFields(debtId: Long? = null): Debt {
        val selectedWalletName = binding.walletSpinner.selectedItem.toString()
        val selectedWallet = mainViewModel.accounts.value.flatMap { it.walletItems }
            .find { it.wallet.name == selectedWalletName }
        val selectedWalletId = selectedWallet?.wallet?.id ?: 0 // Default to 0 if not found

        return Debt(
            id = debtId ?: 0, // Use existing debt id if editing, otherwise 0 for new debt
            name = binding.editTextName.text.toString(),
            amount = binding.editTextAmount.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0,
            accountId = mainViewModel.currentAccount.value!!.account.id, // Set the account ID as needed
            type = DebtType.valueOf(binding.debtTypeTextView.text.toString()),
            date = binding.dateTextView.text.toString().toDateTimestamp(),
            time = binding.timeTextView.text.toString().toTimeTimestamp(),
            description = binding.editTextDescription.text.toString(),
            walletId = selectedWalletId, // Set the wallet ID
            colorId = ColorUtils.getColors(requireContext())[binding.colorSpinner.selectedItemPosition],
        )
    }
}