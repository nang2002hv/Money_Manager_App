package com.example.money_manager_app.fragment.wallet.add_wallet

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.ColorSpinnerAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.databinding.FragmentAddWalletBinding
import com.example.money_manager_app.utils.ColorUtils
import com.example.money_manager_app.utils.IconUtils
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toFormattedDateString
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddWalletFragment :
    BaseFragment<FragmentAddWalletBinding, AddWalletViewModel>(R.layout.fragment_add_wallet) {

    private var wallet: Wallet? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var colorAdapter: ColorSpinnerAdapter
    private lateinit var walletIconBottomSheetFragment: WalletIconBottomSheetFragment

    override fun getVM(): AddWalletViewModel {
        val viewModel: AddWalletViewModel by viewModels()
        return viewModel
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            wallet = it.getParcelable("wallet")
            wallet?.let { getVM().getWalletById(it.id) }
        }
    }

    override fun initToolbar() {
        super.initToolbar()
        binding.backArrowButton.setOnClickListener {
            onBack()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        // Initialize the wallet type spinner
        val walletTypes = WalletType.entries.map { it.name }
        val walletTypeAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, walletTypes
        )
        walletTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.walletTypeSpinner.adapter = walletTypeAdapter

        colorAdapter = ColorSpinnerAdapter(requireContext(), ColorUtils.getColors(requireContext()))
        binding.colorSpinner.adapter = colorAdapter
        binding.colorSpinner.setSelection(0)

        walletIconBottomSheetFragment = WalletIconBottomSheetFragment(
            selectedIcon = wallet?.iconId ?: R.drawable.wallet_1, onIconSelected = { iconId ->
                getVM().setSelectedIconId(iconId) // Update ViewModel with selected icon ID
            }, icons = IconUtils.getWalletIconList()
        )

        binding.iconImageView.setImageResource(wallet?.iconId ?: R.drawable.wallet_1)
        binding.iconImageView.setOnSafeClickListener {
            walletIconBottomSheetFragment.show(parentFragmentManager, "icon_picker")
        }

        // Add TextWatchers to EditTexts
        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.amountEditText.addTextChangedListener(textWatcher)

        // Add listeners for date fields
        binding.etStatementDate.setOnClickListener {
            showDatePickerDialog { date ->
                binding.etStatementDate.text = date
            }
        }
        binding.etDueDate.setOnClickListener {
            showDatePickerDialog { date ->
                binding.etDueDate.text = date
            }
        }

        // Add listener for wallet type spinner
        binding.walletTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    val selectedType = WalletType.entries[position]
                    binding.creditLayout.visibility =
                        if (selectedType == WalletType.CREDIT_CARD) View.VISIBLE else View.GONE
                    binding.generalLayout.visibility =
                        if (selectedType == WalletType.GENERAL) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe wallet data
                getVM().wallet.collect { wallet ->
                    if (wallet != null) {
                        populateFieldsWithWalletData(wallet)
                        binding.saveButton.setOnSafeClickListener {
                            val updatedWallet = buildWalletFromFields(wallet.id)
                            if(updatedWallet.name.isEmpty()||updatedWallet.amount <= 0){
                                Toast.makeText(requireActivity(),getString(R.string.blank_input),
                                    Toast.LENGTH_SHORT).show()
                            }else{
                                getVM().editWallet(updatedWallet)
                                appNavigation.navigateUp()
                            }
                        }
                    } else {
                        binding.saveButton.setOnSafeClickListener {
                            val newWallet = buildWalletFromFields()
                            if(newWallet.name.isEmpty()||newWallet.amount <= 0){
                                Toast.makeText(requireActivity(),getString(R.string.blank_input),
                                    Toast.LENGTH_SHORT).show()
                            }else{
                                if(newWallet.walletType == WalletType.CREDIT_CARD){
                                    if(newWallet.statementDate == null || newWallet.dueDate == null){
                                        Toast.makeText(requireActivity(),getString(R.string.blank_input),
                                            Toast.LENGTH_SHORT).show()
                                        return@setOnSafeClickListener
                                    } else {
                                        getVM().addWallet(newWallet.copy(isExcluded = true))
                                    }
                                } else {
                                    getVM().addWallet(newWallet)
                                }
                                appNavigation.navigateUp()
                            }
                        }
                    }
                }
            }
        }

        // Observe icon selection
        getVM().selectedIcon.observe(viewLifecycleOwner) { iconId ->
            binding.iconImageView.setImageResource(iconId)
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
        val name = binding.nameEditText.text.toString()
        val amount = binding.amountEditText.text.toString()

        binding.saveButton.isEnabled = name.isNotEmpty() && amount.isNotEmpty()
    }

    private fun populateFieldsWithWalletData(wallet: Wallet) {
        binding.nameEditText.setText(wallet.name)
        binding.amountEditText.setText(getString(R.string.money_amount, "", wallet.amount))
        val colorIndex = ColorUtils.getColorIndex(requireContext(), wallet.colorId)
        binding.colorSpinner.setSelection(colorIndex)

        val walletTypes = WalletType.entries.map { it.name }
        val walletTypeIndex = walletTypes.indexOf(wallet.walletType.name)
        if (walletTypeIndex >= 0) {
            binding.walletTypeSpinner.setSelection(walletTypeIndex)
        }

        getVM().setSelectedIconId(wallet.iconId) // Update ViewModel with wallet's icon ID

        // Show or hide credit layout based on wallet type
        binding.creditLayout.visibility =
            if (wallet.walletType == WalletType.CREDIT_CARD) View.VISIBLE else View.GONE
        binding.generalLayout.visibility =
            if (wallet.walletType == WalletType.GENERAL) View.VISIBLE else View.GONE

        wallet.isExcluded?.let { binding.excludeSwitch.isChecked = it }
        // Set statement and due dates
        wallet.statementDate?.let { binding.etStatementDate.text = it.toFormattedDateString() }
        wallet.dueDate?.let { binding.etDueDate.text = it.toFormattedDateString() }
    }

    private fun buildWalletFromFields(walletId: Long? = null): Wallet {
        val walletType = WalletType.valueOf(binding.walletTypeSpinner.selectedItem.toString())
        val statementDate = if (walletType == WalletType.CREDIT_CARD) {
            binding.etStatementDate.text.toString().toFormattedDate()?.time
        } else null
        val dueDate = if (walletType == WalletType.CREDIT_CARD) {
            binding.etDueDate.text.toString().toFormattedDate()?.time
        } else null

        return Wallet(
            id = walletId ?: 0,
            name = binding.nameEditText.text.toString(),
            amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0,
            colorId = ColorUtils.getColors(requireContext())[binding.colorSpinner.selectedItemPosition],
            accountId = mainViewModel.currentAccount.value!!.account.id,
            walletType = walletType,
            iconId = getVM().selectedIcon.value ?: R.drawable.wallet_1,
            isExcluded = if (walletType == WalletType.GENERAL) binding.excludeSwitch.isChecked else null,
            statementDate = statementDate,
            dueDate = dueDate
        )
    }

    private fun String.toFormattedDate(): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            format.parse(this)
        } catch (e: ParseException) {
            null
        }
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSet(date)
            }, year, month, day)

        datePickerDialog.show()
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }
}