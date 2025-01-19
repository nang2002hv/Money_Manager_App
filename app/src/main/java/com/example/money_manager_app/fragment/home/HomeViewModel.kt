package com.example.money_manager_app.fragment.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.TransactionListItem
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.GoalTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import com.example.money_manager_app.utils.groupTransactionsByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val transferRepository: TransferRepository,
    private val goalTransactionRepository: GoalTransactionRepository,
    private val debtTransactionRepository: DebtTransactionRepository,
    private val debtRepository: DebtRepository,
) : BaseViewModel() {
    private val _transactions = MutableStateFlow<List<TransactionListItem>>(emptyList())
    val transactions: StateFlow<List<TransactionListItem>> get() = _transactions

    fun getTransactionsByAccountId(accountId: Long) {
        val goalsFlow = goalTransactionRepository.getGoalTransactionsByAccountId(accountId)
        val transfersFlow = transferRepository.getTransferByAccountId(accountId)
        val debtTransactionsFlow =
            debtTransactionRepository.getDebtTransactionsByAccountId(accountId)
        val debtsFlow = debtRepository.getDebtListByAccountId(accountId)

        viewModelScope.launch {
            combine(
                goalsFlow,
                transfersFlow,
                debtTransactionsFlow,
                debtsFlow
            ) { goals, transfers, debtTransactions, debts ->
                val transactions = mutableListOf<Transaction>()
                transactions.addAll(goals)
                transactions.addAll(transfers)
                transactions.addAll(debtTransactions)
                transactions.addAll(debts)
                transactions
            }.collect {
                _transactions.value = it.groupTransactionsByDate()
            }
        }
    }
}