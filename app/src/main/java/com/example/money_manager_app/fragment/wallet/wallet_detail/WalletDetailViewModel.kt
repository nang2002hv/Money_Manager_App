package com.example.money_manager_app.fragment.wallet.wallet_detail

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.TransactionListItem
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.GoalTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.utils.groupTransactionsByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletDetailViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val walletRepository: WalletRepository,
    private val debtTransactionRepository: DebtTransactionRepository,
    private val transferRepository: TransferRepository,
    private val goalTransactionRepository: GoalTransactionRepository,
) : BaseViewModel() {

    private val _walletDetailItem = MutableStateFlow<WalletDetailItem?>(null)
    val walletDetailItemState: StateFlow<WalletDetailItem?> get() = _walletDetailItem

    fun getWalletDetailItem(walletId: Long) {
        viewModelScope.launch {
            walletRepository.getWalletById(walletId).collect { wallet ->
                _walletDetailItem.value = when (wallet.walletType) {
                    WalletType.CREDIT_CARD -> WalletDetailItem.CreditItem(
                        wallet = wallet,
                        name = wallet.name,
                        iconId = wallet.iconId,
                        colorId = wallet.colorId,
                        creditLimit = wallet.amount,
                        availableCredit = wallet.amount, // Can be updated later with real-time data
                        statementDate = wallet.statementDate ?: 0L,
                        dueDate = wallet.dueDate ?: 0L,
                        expense = 0,
                        transactions = emptyList()
                    )

                    else -> WalletDetailItem.GeneralItem(
                        wallet = wallet,
                        name = wallet.name,
                        iconId = wallet.iconId,
                        colorId = wallet.colorId,
                        initialBalance = wallet.amount,
                        currentBalance = wallet.amount, // To be updated
                        income = 0,
                        expense = 0,
                        transfer = 0,
                        isExcluded = wallet.isExcluded ?: false,
                        transactions = emptyList()
                    )
                }
            }
        }
    }

    fun getTransactionByAccountIdAndWalletId(accountId: Long, walletId: Long) {
        val goalsFlow =
            goalTransactionRepository.getGoalTransactionsByAccountIdAndWalletId(accountId, walletId)
        val transfersFlow =
            transferRepository.getTransferWithCategoryByAccountIdAndWalletId(accountId, walletId)
        val debtsFlow = debtRepository.getDebtListByAccountIdAndWalletId(accountId, walletId)
        val debtTransactionsFlow =
            debtTransactionRepository.getDebtTransactionsByAccountIdAndWalletId(accountId, walletId)

        viewModelScope.launch {
            combine(
                goalsFlow, transfersFlow, debtsFlow, debtTransactionsFlow
            ) { goals, transfers, debts, debtTransactions ->
                (goals + transfers + debts + debtTransactions)
            }.collect { groupedTransactions ->
                _walletDetailItem.value?.let { wallet ->
                    updateWalletDetails(wallet, groupedTransactions)
                }
            }
        }
    }

    private fun updateWalletDetails(wallet: WalletDetailItem, transactions: List<Transaction>) {

        var income = 0
        var expense = 0
        var transfer = 0
        var expenseAmount = 0.0
        var incomeAmount = 0.0
        var transferInAmount = 0.0
        var transferOutAmount = 0.0
        var transferFee = 0.0

        transactions.forEach { transaction ->
            when (transaction) {
                is Debt -> {
                    if (transaction.type == DebtType.PAYABLE) {
                        income += 1
                        incomeAmount += transaction.amount
                    } else {
                        expense += 1
                        expenseAmount += transaction.amount
                    }
                }

                is DebtTransaction -> {
                    if (transaction.action in listOf(DebtActionType.DEBT_INCREASE, DebtActionType.DEBT_COLLECTION, DebtActionType.LOAN_INTEREST)) {
                        income += 1
                        incomeAmount += transaction.amount
                    } else {
                        expense += 1
                        expenseAmount += transaction.amount
                    }
                }

                is Transfer -> {
                    when (transaction.typeOfExpenditure) {
                        TransferType.Income -> {
                            income += 1
                            incomeAmount += transaction.amount
                        }

                        TransferType.Expense -> {
                            expense += 1
                            expenseAmount += transaction.amount
                        }

                        TransferType.Transfer -> {
                            transfer += 1
                            if(transaction.toWalletId == wallet.wallet.id) {
                                transferInAmount += transaction.amount
                            } else {
                                transferOutAmount += transaction.amount
                                transferFee += transaction.fee

                            }
                        }
                    }
                }

                is GoalTransaction -> {
                    if (transaction.type == GoalInputType.WITHDRAW) {
                        income += 1
                        incomeAmount += transaction.amount
                    } else {
                        expense += 1
                        expenseAmount += transaction.amount
                    }
                }
            }
        }
        val item = when(wallet){
            is WalletDetailItem.GeneralItem -> {
                wallet.copy(
                    income = income,
                    expense = expense,
                    transfer = transfer,
                    currentBalance = wallet.initialBalance + incomeAmount - expenseAmount + transferInAmount - transferOutAmount - transferFee,
                    transactions = transactions.groupTransactionsByDate(),
                )
            }
            is WalletDetailItem.CreditItem -> {
                wallet.copy(
                    expense = expense,
                    transactions = transactions.groupTransactionsByDate(),
                    availableCredit = wallet.creditLimit - expenseAmount - transferOutAmount - transferFee
                )
            }
        }


        _walletDetailItem.value = item
    }
}

sealed class WalletDetailItem(
    open val wallet: Wallet,
    open val name: String,
    open val iconId: Int,
    open val colorId: Int,
    open var transactions: List<TransactionListItem>
) {
    data class GeneralItem(
        override val wallet: Wallet,
        override val name: String,
        override val iconId: Int,
        override val colorId: Int,
        val initialBalance: Double,
        var currentBalance: Double,
        var income: Int,
        var expense: Int,
        var transfer: Int,
        val isExcluded: Boolean,
        override var transactions: List<TransactionListItem>
    ) : WalletDetailItem(wallet, name, iconId, colorId, transactions)

    data class CreditItem(
        override val wallet: Wallet,
        override val name: String,
        override val iconId: Int,
        override val colorId: Int,
        val creditLimit: Double,
        var availableCredit: Double,
        val statementDate: Long,
        val dueDate: Long,
        var expense: Int,
        override var transactions: List<TransactionListItem>
    ) : WalletDetailItem(wallet, name, iconId, colorId, transactions)
}
