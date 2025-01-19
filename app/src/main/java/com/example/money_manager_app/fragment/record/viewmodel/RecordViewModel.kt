package com.example.money_manager_app.fragment.record.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val debtRepository: DebtRepository,
    private val debtTransactionRepository: DebtTransactionRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): BaseViewModel() {

    private val _transaction : MutableStateFlow<Transaction> = MutableStateFlow(Transaction(0, 0, "", 0.0, 0L, 0L,0,0))
    val transaction : MutableStateFlow<Transaction> get() = _transaction

    fun setTransaction(transaction: Transaction, idAccount: Long) {
        if(transaction != null) {
            viewModelScope.launch {
                if(transaction is Transfer) {
                    transferRepository.getTransfer(idAccount, transaction.id).collect {
                        _transaction.value = it
                    }
                }
                if (transaction is DebtTransaction) {
                    debtTransactionRepository.getDebtTransactionByIdAccountAndIdDebtTrasaction(idAccount, transaction.id).collect {
                        _transaction.value = it
                    }
                }
            }
        }
    }

    fun deleteTransfer(transfer: Transfer) {
        viewModelScope.launch(ioDispatcher) {
            transferRepository.deleteTransfer(transfer.id)
        }
    }

    fun deleteDebt(debtId: Long) {
        viewModelScope.launch(ioDispatcher) {
            debtRepository.deleteDebt(debtId)
        }
    }

    fun deleteDebtTransaction(debtTransactionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            debtTransactionRepository.deleteDebtTransaction(debtTransactionId)
        }
    }

}