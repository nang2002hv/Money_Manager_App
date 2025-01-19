package com.example.money_manager_app.fragment.record.view

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.data.model.toWallet
import com.example.money_manager_app.databinding.AlertDialogBinding
import com.example.money_manager_app.databinding.FragmentRecordBinding
import com.example.money_manager_app.fragment.record.viewmodel.RecordViewModel
import com.example.money_manager_app.fragment.add.view.expense.ExpenseViewModel
import com.example.money_manager_app.fragment.add.view.income.IncomeViewModel
import com.example.money_manager_app.fragment.add.view.transfer.TransferViewModel
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import com.example.money_manager_app.utils.toFormattedDateString
import com.example.money_manager_app.utils.toFormattedTimeString
import com.example.money_manager_app.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class RecordFragment  : BaseFragment<FragmentRecordBinding, RecordViewModel>(R.layout.fragment_record) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val expenseViewModel : ExpenseViewModel by activityViewModels()
    private val incomeViewModel : IncomeViewModel by activityViewModels()
    private val transferViewModel : TransferViewModel by activityViewModels()
    private val addViewModel : AddViewModel by activityViewModels()

    override fun getVM(): RecordViewModel {
        val vm: RecordViewModel by activityViewModels()
        return vm
    }

    private lateinit var transaction: Transaction

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        observeData()
        back()
        delete()
        edit()
    }

    private fun edit() {
        binding.ivEdit.setOnClickListener {
            if(transaction != null){
                addViewModel.setCheckEdit(true)
                if(transaction is Transfer){
                    if((transaction as Transfer).typeOfExpenditure == TransferType.Transfer){
                        setIncomeExpense(transaction)
                        findNavController().navigate(R.id.addFragment)
                    }
                    if((transaction as Transfer).typeOfExpenditure == TransferType.Income){
                        incomeViewModel.setAmount(transaction.amount)
                        setIncomeExpense(transaction)
                        findNavController().navigate(R.id.addFragment)
                    }
                    if((transaction as Transfer).typeOfExpenditure == TransferType.Expense){
                        setIncomeExpense(transaction)
                        expenseViewModel.setAmount(transaction.amount)
                        findNavController().navigate(R.id.addFragment)
                    }
                }

            }
        }
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    private fun setIncomeExpense(transaction: Transaction){
        if(transaction is Transfer){
            if(transaction.typeOfExpenditure == TransferType.Income){
                incomeViewModel.setId(transaction.id)
                incomeViewModel.setAmount(transaction.amount)
                incomeViewModel.setOldAmount(transaction.amount)
                incomeViewModel.setDescriptor(transaction.description)
                addViewModel.setIdCategory(transaction.categoryId.toInt())
                addViewModel.setPosition(0)
                val date = transaction.date.toFormattedDateString()
                val time = transaction.time.toFormattedTimeString()
                incomeViewModel.setDateAndTime(date, time)
                incomeViewModel.setMomo(transaction.memo)
                val imgFile = File(transaction.linkImg)
                if (imgFile.exists()) {
                    try {
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(imgFile))
                        incomeViewModel.setBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val fromWallet = mainViewModel.currentAccount.value?.walletItems?.find { it.wallet.id == transaction.walletId }?.toWallet()
                if(fromWallet != null){
                    val listWallet = mutableListOf<Wallet>()
                    listWallet.add(fromWallet)
                    incomeViewModel.setFromWallet(listWallet)
                    incomeViewModel.setOldWallet(fromWallet)
                }

            }
            if(transaction.typeOfExpenditure == TransferType.Expense){
                expenseViewModel.setId(transaction.id)
                expenseViewModel.setAmount(transaction.amount)
                expenseViewModel.setOldAmount(transaction.amount)
                expenseViewModel.setDescriptor(transaction.description)
                addViewModel.setIdCategory(transaction.categoryId.toInt()-9)
                addViewModel.setPosition(1)
                expenseViewModel.setMomo(transaction.memo)
                val date = transaction.date.toFormattedDateString()
                val time = transaction.time.toFormattedTimeString()
                expenseViewModel.setDateAndTime(date, time)
                val imgFile = File(transaction.linkImg)
                if (imgFile.exists()) {
                    try {
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(imgFile))
                        expenseViewModel.setBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val fromWallet = mainViewModel.currentAccount.value?.walletItems?.find { it.wallet.id == transaction.walletId }?.toWallet()
                if(fromWallet != null){
                    val listWallet = mutableListOf<Wallet>()
                    listWallet.add(fromWallet)
                    expenseViewModel.setFromWallet(listWallet)
                    expenseViewModel.setOldWallet(fromWallet)
                }
            }

            if(transaction.typeOfExpenditure == TransferType.Transfer){
                transferViewModel.setId(transaction.id)
                transferViewModel.setAmount(transaction.amount)
                transferViewModel.setOldAmount(transaction.amount)
                transferViewModel.setDescriptor(transaction.description)
                addViewModel.setPosition(2)
                transferViewModel.setMomo(transaction.memo)
                transferViewModel.setFee(transaction.fee)
                val date = transaction.date.toFormattedDateString()
                val time = transaction.time.toFormattedTimeString()
                transferViewModel.setDateAndTime(date, time)
                val imgFile = File(transaction.linkImg)
                if (imgFile.exists()) {
                    try {
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(imgFile))
                        transferViewModel.setBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val FromWallet = mainViewModel.currentAccount.value?.walletItems?.find { it.wallet.id == transaction.walletId }?.toWallet()
                if(FromWallet != null){
                    val listWallet = mutableListOf<Wallet>()
                    listWallet.add(FromWallet)
                    transferViewModel.setOldWallet(FromWallet)
                    transferViewModel.setFromWallet(listWallet)
                }
                val toWallet = mainViewModel.currentAccount.value?.walletItems?.find { it.wallet.id == transaction.toWalletId }?.toWallet()
                if(toWallet != null){
                    val listWallet = mutableListOf<Wallet>()
                    listWallet.add(toWallet)
                    transferViewModel.setToWallet(listWallet)
                    transferViewModel.setOldToWallet(toWallet)
                }
            }
        }
    }

    private fun delete() {
        binding.ivDelete.setOnClickListener {
            if( transaction!= null){
                val AlertDialogBinding = AlertDialogBinding.inflate(layoutInflater)

                val alert = AlertDialog.Builder(requireContext()).create()
                alert.setView(AlertDialogBinding.root)
                alert.setCancelable(true)
                AlertDialogBinding.deleteImageView.setOnClickListener {
                    if(transaction is Transfer){
                        getVM().deleteTransfer(transaction as Transfer)
                    }
                    if(transaction is DebtTransaction){
                        getVM().deleteDebtTransaction((transaction as DebtTransaction).id)
                    }
                    if(transaction is DebtTransaction){
                        getVM().deleteDebt((transaction as DebtTransaction).debtId)
                    }
                    alert.dismiss()
                    findNavController().popBackStack()
                }
                AlertDialogBinding.cannelImageView.setOnClickListener {
                    alert.dismiss()
                }
                alert.show()
            }
        }
    }

    private fun back() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().transaction.collect {
                    showHideLoading(it)
                }
            }
        }

    }

    private fun showHideLoading(transaction: Transaction){
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
       if(transaction != null) {
           if(transaction.iconId != null){
               transaction.iconId?.let { binding.ivIcon.setImageResource(it) }
           }
           binding.amountLabel.text = context?.getString(R.string.money_amount,currencySymbol, transaction.amount)
           binding.dateLabel.text = transaction.date.toFormattedDateString() + " " + transaction.time.toFormattedTimeString()
           val wallet = mainViewModel.currentAccount.value?.walletItems
           val fromWallet = wallet?.find { it.wallet.id == transaction.walletId }
           when (transaction) {
               is Transfer -> {
                   binding.categoryLabel.visibility = android.view.View.VISIBLE
                   binding.categoryLabel.text = mainViewModel.categories.value.find { it.id == transaction.categoryId }?.name
                   binding.tvDescription.text = transaction.description
                   if(transaction?.memo != null){
                       binding.memoLabel.text = transaction.memo
                       binding.memoLabel.visibility = android.view.View.VISIBLE
                       binding.memoTitleLabel.visibility = android.view.View.VISIBLE
                   }
                   if (transaction.typeOfExpenditure == TransferType.Transfer){
                       if(transaction?.fee != null){
                           binding.feeLabel.text = transaction.fee.toString()
                           binding.feeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                           binding.feeLabel.visibility = android.view.View.VISIBLE
                           binding.feeTitleLabel.visibility = android.view.View.VISIBLE
                       }

                       val toWallet = wallet?.find { it.wallet.id == transaction.toWalletId }
                       binding.walletLabel.text = fromWallet?.wallet?.name + " -> " + toWallet?.wallet?.name
                       binding.typeLabel.text = transaction.typeOfExpenditure.name
                   } else {
                       if(transaction.typeOfExpenditure == TransferType.Income){
                           binding.typeLabel.text = getString(R.string.income)
                           binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_2))
                       } else {
                           binding.typeLabel.text = getString(R.string.expense)
                           binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                       }
                       binding.walletLabel.text = fromWallet?.wallet?.name
                   }
                   binding.ivImg.visibility = android.view.View.VISIBLE
                   val file = File(transaction.linkImg)
                   if (file.exists()) {
                       val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                       binding.ivImg.setImageBitmap(bitmap)
                   }
               }

               is DebtTransaction-> {
                   binding.categoryLabel.text = transaction.action.name
                   binding.tvDescription.text = transaction.action.name
                     binding.walletLabel.text = fromWallet?.wallet?.name
                   if(transaction.action in listOf(DebtActionType.DEBT_INCREASE, DebtActionType.DEBT_COLLECTION, DebtActionType.LOAN_INTEREST)){
                       binding.typeLabel.text = getString(R.string.income)
                       binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_2))
                   }else{
                       binding.typeLabel.text = getString(R.string.expense)
                       binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                   }
               }

               is GoalTransaction -> {
                   if(transaction.type == GoalInputType.WITHDRAW){
                       binding.tvDescription.text = GoalInputType.WITHDRAW.name
                       binding.typeLabel.text = getString(R.string.withdraw)
                       binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_2))
                   }else{
                       binding.tvDescription.text = GoalInputType.DEPOSIT.name
                       binding.typeLabel.text = getString(R.string.deposit)
                       binding.amountLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                   }
               }
           }
       }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        transaction = arguments?.getParcelable<Transaction>("transaction") as Transaction
        if (transaction != null) {
            getVM().setTransaction(transaction, mainViewModel.currentAccount.value?.account!!.id)
        }
    }

}