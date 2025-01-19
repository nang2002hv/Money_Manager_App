package com.example.money_manager_app.fragment.statistic.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.CalendarSummary
import com.example.money_manager_app.data.model.Stats
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.data.repository.CategoryRepository
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


@HiltViewModel
class StatisticViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val walletRepository: WalletRepository,
    private val debtRepository: DebtRepository,
    private val debtTransacitonRepository : DebtTransactionRepository,
    private val transferRepository: TransferRepository,
    private val categoryRepository: CategoryRepository,
    ) : BaseViewModel() {

    private var _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets get() = _wallets

    private var _listStatsIncome = MutableStateFlow<List<Stats>>(emptyList())

    private var _listStatsExpense = MutableStateFlow<List<Stats>>(emptyList())
    val listStatsExpense get() = _listStatsExpense

    private var _calendarSummary = MutableStateFlow(CalendarSummary(0.0, 0.0))
    val calendarSummary get() = _calendarSummary

    private var _listCategory = MutableStateFlow<List<Category>>(emptyList())
    val listCategory : StateFlow<List<Category>> get() = _listCategory

    private var _listTransaction = mutableListOf<Transaction>()
    val listTransaction = _listTransaction

    private var _pairWallet = MutableStateFlow(Pair(0.0,0.0))
    val pairWallet get() = _pairWallet

    private fun setStatsIncome(stats: List<Stats>) {
        _listStatsIncome.value = stats
    }

    fun setWallets(wallets: List<Wallet>) {
        _wallets.value = wallets
    }

    private fun setStatsExpense(stats: List<Stats>) {
        _listStatsExpense.value = stats
    }

    fun getWallets(id: Long, wallets: List<Wallet>, startDate: Long ?, endDate: Long ?) {
        viewModelScope.launch {
            val flow = if (startDate == null && endDate == null) {
                walletRepository.getWalletItemsByUserId(id)
            } else {
                val start = startDate ?: 0
                val end = endDate ?: System.currentTimeMillis()
                walletRepository.getWalletItemsByUserId(id, start, end)
            }
            flow.collect { list ->
                var openingBalance = 0.0
                var endingBalance = 0.0
                for (wallet in list) {
                    if(wallets.contains(wallet.wallet)){
                        openingBalance += if(startDate == null && endDate == null){
                            wallet.wallet.amount
                        } else {
                            wallet.startingAmount
                        }
                        endingBalance += wallet.endingAmount
                    }
                }

                _pairWallet.value = Pair(openingBalance,endingBalance)
            }
        }
    }




    private fun setCalendarSummary(calendarSummary: CalendarSummary) {
        _calendarSummary.value = calendarSummary
    }

    fun getCalendarSummary(listWallet : List<Wallet>, idAccount : Long){
        viewModelScope.launch(ioDispatcher){
            val listTrasaction = mutableListOf<Transaction>()
            for(wallet in listWallet){
                listTrasaction.addAll(getDebtTransaction(wallet.id,idAccount))
                listTrasaction.addAll(getTransfer(wallet.id,idAccount))
                listTrasaction.addAll(getDebt(wallet.id,idAccount))

            }
            getStats(listTrasaction)
            setCalendarSummary(totalCalendarSummary(listTrasaction))

        }
    }

    private fun getStats(listTransaction: List<Transaction>){
        _listTransaction = listTransaction.toMutableList()
        val categoryList = categoryRepository.getAllCategory()
        val listStatsIncome = mutableListOf<Stats>()
        val listStatsExpense: MutableList<Stats> = mutableListOf()
        var id = 0
        for(category in categoryList){
            id++
            if(category.type == CategoryType.INCOME || category.type == CategoryType.PAYABLE || category.type == CategoryType.DEBT_COLLECTION || category.type == CategoryType.DEBT_INCREASE || category.type == CategoryType.LOAN_INTEREST){
                listStatsIncome.add(Stats(category.name,category.colorId,category.id,0.0,0.0,id,0,category.type,category.iconId,false))
            } else {
                listStatsExpense.add(Stats(category.name,category.colorId,category.id,0.0,0.0,id,0,category.type,category.iconId,false))
            }
        }

        for(transaction in listTransaction){
            when(transaction){
                is Transfer -> {
                    if(transaction.typeOfExpenditure == TransferType.Income){
                        for(stats in listStatsIncome){
                            if(stats.icon == transaction.categoryId){
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if(transaction.typeOfExpenditure == TransferType.Expense){
                        for(stats in listStatsExpense) {
                            if (stats.icon == transaction.categoryId) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }

                    if(transaction.typeOfExpenditure == TransferType.Transfer) {
                        for (stats in listStatsExpense) {
                            if (stats.type == CategoryType.TRANSFER && transaction.fee > 0) {
                                stats.amount += transaction.fee
                                stats.trans++
                            }
                        }
                    }

                }

                is Debt -> {
                    if(transaction.type == DebtType.RECEIVABLE) {
                        for (stats in listStatsExpense) {
                            if (stats.type == CategoryType.RECEIVABLE) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if(transaction.type == DebtType.PAYABLE) {
                        for (stats in listStatsIncome) {
                            if (stats.type == CategoryType.PAYABLE) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                }

                is DebtTransaction -> {
                    if(transaction.action == DebtActionType.DEBT_INCREASE){
                        for(stats in listStatsIncome){
                            if(stats.type == CategoryType.DEBT_INCREASE){
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if(transaction.action == DebtActionType.DEBT_COLLECTION) {
                        for (stats in listStatsIncome) {
                            if (stats.type == CategoryType.DEBT_COLLECTION) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if (transaction.action == DebtActionType.LOAN_INCREASE) {
                        for (stats in listStatsExpense) {
                            if (stats.type == CategoryType.LOAN_INCREASE) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if (transaction.action == DebtActionType.REPAYMENT) {
                        for (stats in listStatsExpense) {
                            if (stats.type == CategoryType.PAYABLE) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if (transaction.action == DebtActionType.LOAN_INTEREST) {
                        for (stats in listStatsIncome) {
                            if (stats.type == CategoryType.LOAN_INTEREST) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                    if (transaction.action == DebtActionType.DEBT_INTEREST) {
                        for (stats in listStatsExpense) {
                            if (stats.type == CategoryType.DEBT_INTEREST) {
                                stats.amount += transaction.amount
                                stats.trans++
                            }
                        }
                    }
                }
            }
        }

        listStatsExpense.removeAll { it.trans == 0}
        listStatsIncome.removeAll {it.trans == 0}
        var totalAmountIncome = 0.0
        var totalAmountExpense = 0.0

        for (stats in listStatsIncome) {
            totalAmountIncome += stats.amount
        }

        for (stats in listStatsExpense) {
            totalAmountExpense += stats.amount
        }

        if (listStatsExpense.size > 1) {
            for (i in 0 until listStatsExpense.size - 1) {
                val stat = listStatsExpense[i]
                val percent = (stat.amount / totalAmountExpense) * 100
                val bigDecimal = BigDecimal(percent).setScale(2, RoundingMode.HALF_UP)
                val number = bigDecimal.toDouble()
                stat.percent = number
            }
            listStatsExpense[listStatsExpense.size - 1].percent = 100 - listStatsExpense.sumOf { it.percent }
        } else {
            if(listStatsExpense.size == 1) listStatsExpense[0].percent = 100.0
        }

        if (listStatsIncome.size > 1) {
            for(i in 0 until listStatsIncome.size - 1) {
                val stat = listStatsIncome[i]
                val percent = (stat.amount / totalAmountIncome) * 100
                val bigDecimal = BigDecimal(percent).setScale(2, RoundingMode.HALF_UP)
                val number = bigDecimal.toDouble()
                stat.percent = number
            }
            listStatsIncome[listStatsIncome.size - 1].percent = 100 - listStatsIncome.sumOf { it.percent }
        } else {
            if(listStatsIncome.size == 1) listStatsIncome[0].percent = 100.0
        }

        listStatsExpense.sortByDescending { it.amount }
        listStatsIncome.sortByDescending { it.amount }

        setStatsIncome(listStatsIncome)
        setStatsExpense(listStatsExpense)
    }

    fun getCalendarSummary(listWallet : List<Wallet>, startDate : Long, endDate : Long, idAccount : Long){
        viewModelScope.launch(ioDispatcher){
            val listTrasaction = mutableListOf<Transaction>()
            for(wallet in listWallet){
                listTrasaction.addAll(getDebtTransaction(wallet.id,startDate,endDate,idAccount))
                listTrasaction.addAll(getTransfer(wallet.id,startDate,endDate,idAccount))
                listTrasaction.addAll(getDebt(wallet.id,startDate,endDate,idAccount))

            }
            getStats(listTrasaction)
            setCalendarSummary(totalCalendarSummary(listTrasaction))
        }
    }

    private fun getDebtTransaction(idWallet : Long, idAccount : Long) : List<Transaction>{
        val debtTransaction = debtTransacitonRepository.getDebtTransactionsByAccountIdAndWallet(idAccount,idWallet)
        val listTransaction = mutableListOf<Transaction>()
        for(debt in debtTransaction){
            listTransaction.add(debt)
        }
        return listTransaction
    }

    private fun getTransfer(idWallet : Long, idAccount : Long) : List<Transaction>{
        val transfer = transferRepository.getTransferByWalletAndDayStartAndDayEnd(idAccount,idWallet)
        val listTransaction = mutableListOf<Transaction>()
        for(item in transfer){
            listTransaction.add(item)
        }
        return listTransaction
    }

    private fun getDebt(idWallet : Long, idAccount : Long) : List<Transaction>{
        val debt = debtRepository.getDebtListByAccountIdAndWallet(idAccount,idWallet)
        val listTransaction = mutableListOf<Transaction>()
        for(item in debt){
            listTransaction.add(item)
        }
        return listTransaction
    }


    private fun getDebtTransaction(idWallet : Long, startDate : Long, endDate : Long, idAccount : Long) : List<Transaction>{
        val debtTransaction = debtTransacitonRepository.getDebtTransactionByWalletAndDayStartAndDayEnd(idAccount,idWallet,startDate,endDate)
        val listTransaction = mutableListOf<Transaction>()
        for(debt in debtTransaction){
            listTransaction.add(debt)
        }
        return listTransaction
    }

    private fun getTransfer(idWallet : Long, startDate : Long, endDate : Long, idAccount : Long) :  List<Transaction>{
        val transfer = transferRepository.getTransferByWalletAndDayStartAndDayEnd(idAccount,idWallet,startDate,endDate)
        val listTransaction = mutableListOf<Transaction>()
        for(item in transfer){
            listTransaction.add(item)
        }
        return listTransaction
    }

    private fun getDebt(idWallet : Long, startDate : Long, endDate : Long, idAccount : Long) :  List<Transaction>{
        val debt = debtRepository.getDebtByWalletAndDayStartAndDayEnd(idAccount,idWallet,startDate,endDate)
        val listTransaction = mutableListOf<Transaction>()
        for(item in debt){
            listTransaction.add(item)
        }
        return listTransaction
    }

    private fun totalCalendarSummary(listTransaction : List<Transaction>) : CalendarSummary{
        var totalIncome = 0.0
        var totalExpense = 0.0
        for(transaction in listTransaction){
            when(transaction){
                is Transfer -> {
                    if(transaction.typeOfExpenditure == TransferType.Income){
                        totalIncome += transaction.amount
                    } else {
                        if(transaction.typeOfExpenditure == TransferType.Expense){
                            totalExpense += transaction.amount
                        } else
                        {
                            if(transaction.typeOfExpenditure == TransferType.Transfer){
                                totalExpense += transaction.fee
                            }
                        }
                    }
                }

                is Debt -> {
                    if (transaction.type == DebtType.RECEIVABLE) {
                        totalExpense += transaction.amount
                    } else {
                        totalIncome += transaction.amount
                    }
                }

                is DebtTransaction -> {
                    if (transaction.action == DebtActionType.DEBT_INCREASE || transaction.action == DebtActionType.DEBT_COLLECTION  || transaction.action == DebtActionType.LOAN_INTEREST) {
                        totalIncome += transaction.amount
                    } else {
                        totalExpense += transaction.amount
                    }
                }
            }
        }
        return CalendarSummary(totalIncome,totalExpense)
    }

}