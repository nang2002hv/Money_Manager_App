package com.example.money_manager_app.fragment.wallet.add_goal_transaction

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.repository.GoalTransactionRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGoalTransactionViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val goalTransactionRepository: GoalTransactionRepository
) : BaseViewModel() {
    fun addGoalTransaction(goalTransaction: GoalTransaction) {
        if(goalTransaction.amount > 0) {
            viewModelScope.launch(ioDispatcher) {
                goalTransactionRepository.insertGoalTransaction(goalTransaction)
            }
        }
    }

    fun editGoalTransaction(goalTransaction: GoalTransaction) {
        if(goalTransaction.amount > 0) {
            viewModelScope.launch(ioDispatcher) {
                goalTransactionRepository.updateGoalTransaction(goalTransaction)
            }
        }
    }

    fun deleteGoalTransaction(goalTransactionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            goalTransactionRepository.deleteGoalTransaction(goalTransactionId)
        }
    }
}