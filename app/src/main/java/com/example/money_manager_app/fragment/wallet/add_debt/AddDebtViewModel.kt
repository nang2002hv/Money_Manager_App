package com.example.money_manager_app.fragment.wallet.add_debt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtDetail
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDebtViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val debtRepository: DebtRepository
) : BaseViewModel() {
    private val _debt : MutableStateFlow<DebtDetail?> = MutableStateFlow(null)
    val debt : StateFlow<DebtDetail?> get() = _debt
    private val _currentDebtTypeIndex : MutableLiveData<Int> = MutableLiveData(0)
    val currentDebtTypeIndex : LiveData<Int> get() = _currentDebtTypeIndex

    fun getDebtDetails(debtId: Long) {
        viewModelScope.launch(ioDispatcher) {
            debtRepository.getDebtDetailsByDebtId(debtId).collect{
                it?.let {
                    _debt.value = it
                    initDebtTypeIndex(if (it.debt.type == DebtType.RECEIVABLE) 0 else 1)
                }
            }
        }
    }


    fun addDebt(debt: Debt): Long {
        return if (debt.name.isNotEmpty() && debt.amount > 0 && debt.description.isNotEmpty()) {
            var debtId: Long = -1
            viewModelScope.launch(ioDispatcher) {
                debtId = debtRepository.insertDebt(debt)
            }
            debtId
        } else {
            -1L
        }
    }

    fun editDebt(debt: Debt) {
        viewModelScope.launch(ioDispatcher) {
            debtRepository.editDebt(debt)
        }
    }

    fun toggleCurrentDebtTypeIndex() {
        _currentDebtTypeIndex.postValue(if (_currentDebtTypeIndex.value == 0) 1 else 0)
    }

    fun initDebtTypeIndex(index: Int) {
        _currentDebtTypeIndex.postValue(index)
    }
}