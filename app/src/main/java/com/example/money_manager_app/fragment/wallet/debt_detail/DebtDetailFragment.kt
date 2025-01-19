package com.example.money_manager_app.fragment.wallet.debt_detail

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.TransactionAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.toWallet
import com.example.money_manager_app.databinding.AlertDialogBinding
import com.example.money_manager_app.databinding.FragmentDebtDetailBinding
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DebtDetailFragment :
    BaseFragment<FragmentDebtDetailBinding, DebtDetailViewModel>(R.layout.fragment_debt_detail) {

    private var debt: Debt? = null
    override fun getVM(): DebtDetailViewModel {
        val vm: DebtDetailViewModel by viewModels()
        return vm
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            debt = it.getParcelable<Debt>("debt")
            getVM().getDebtDetails(debt!!.id)
        }

    }

    override fun initToolbar() {
        super.initToolbar()
        binding.backButton.setOnSafeClickListener {
            onBack()
        }
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val currentCurrencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)
        val wallets = mainViewModel.currentAccount.value!!.walletItems.map { it.toWallet() }
        adapter = TransactionAdapter(
            requireContext(),
            currentCurrencySymbol,
            wallets,
            listOf(),
            ::openAddDebtTransactionScreen
        )
        binding.debtTransactionRv.adapter = adapter
        binding.debtTransactionRv.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun bindingStateView() {
        super.bindingStateView()

        val currentCurrencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().debtDetailItem.collect {
                    it?.let {
                        binding.apply {
                            nameLabel.text = it.title
                            descriptionLabel.text = it.description
                            spentTitleLabel.text =
                                if (it.type == DebtType.PAYABLE) getString(R.string.paid) else getString(
                                    R.string.received
                                )
                            spentLabel.text = getString(
                                R.string.money_amount, currentCurrencySymbol, it.paidAmount
                            )
                            remainLabel.text = getString(
                                R.string.money_amount, currentCurrencySymbol, it.remainingAmount
                            )
                            amountLabel.text = getString(
                                R.string.money_amount, currentCurrencySymbol, it.totalAmount
                            )
                            percentLabel.text =
                                if (it.progress > 100) getString(R.string.overpaid) else getString(
                                    R.string.percent, it.progress
                                )
                            circleLabel.setImageResource(it.iconId)
                            progressBar.progress = it.progress
                            dateLabel.text = it.date
                            progressBar.indeterminateTintList =
                                ContextCompat.getColorStateList(requireContext(), it.colorId)
                            walletLabel.text =
                                mainViewModel.currentAccount.value!!.walletItems.map { it.toWallet() }
                                    .find { wallet -> wallet.id == it.walletId }?.name
                            adapter.submitList(it.transactions)
                        }
                    }
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.editButton.setOnSafeClickListener {
            val debtToSent = getVM().debtInfo.value!!.debt
            appNavigation.openDebtDetailToAddDebtScreen(Bundle().apply {
                putParcelable("debt", debtToSent)
            })
        }
        binding.deleteButton.setOnSafeClickListener {
            val alertDialogBinding = AlertDialogBinding.inflate(layoutInflater)

            val alert = AlertDialog.Builder(requireContext()).create()
            alert.setView(alertDialogBinding.root)
            alert.setCancelable(true)
            alertDialogBinding.deleteImageView.setOnClickListener {
                debt?.let {
                    getVM().deleteDebt(it.id)
                }
                alert.dismiss()
                appNavigation.navigateUp()
            }
            alertDialogBinding.cannelImageView.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }

        binding.addDebtAction.setOnSafeClickListener {
            val debtToSent = getVM().debtInfo.value!!.debt
            appNavigation.openDebtDetailToAddDebtTransactionScreen(Bundle().apply {
                putParcelable("debt", debtToSent)
            })
        }

    }

    private fun openAddDebtTransactionScreen(transaction: Transaction) {
        appNavigation.openDebtDetailScreenToRecordScreen(Bundle().apply {
            putParcelable("debt", debt)
            putParcelable("transaction", transaction as DebtTransaction)
        })
    }
}

