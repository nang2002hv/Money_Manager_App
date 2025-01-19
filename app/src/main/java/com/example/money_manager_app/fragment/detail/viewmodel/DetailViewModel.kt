package com.example.money_manager_app.fragment.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val debtRepository: DebtRepository,
    private val DebtTransactionRepository: DebtTransactionRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _detailTransaction : MutableStateFlow<List<Transaction>> = MutableStateFlow(emptyList())
    val detailTransaction : StateFlow<List<Transaction>> get() = _detailTransaction


    fun setDetailDayTransaction(list : List<Transaction>) {
        _detailTransaction.value = list
    }


    fun getTransaction(date : Long, idAccount : Long) {
        viewModelScope.launch(ioDispatcher) {
            val transfers = transferRepository.getAllTransfer(date).first()
            val debts = debtRepository.getDebtsByDateAndAccountId(date, idAccount).first()
            val debtTransactions = DebtTransactionRepository.getDebtTransactionsByDateAndAccountId(date, idAccount).first()
            val list = mutableListOf<Transaction>()
            list.addAll(transfers)
            list.addAll(debts)
            list.addAll(debtTransactions)
            setDetailDayTransaction(list)
        }

    }
}