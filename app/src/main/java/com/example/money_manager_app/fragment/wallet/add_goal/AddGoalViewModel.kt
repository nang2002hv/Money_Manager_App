package com.example.money_manager_app.fragment.wallet.add_goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.data.repository.GoalRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val goalRepository: GoalRepository
): BaseViewModel() {
    private var _name = ""
    private var _amount = 0.0
    private var _targetDate = 0L
    private var _isSavable = MutableLiveData(false)
    val isSavable : LiveData<Boolean> get() = _isSavable

    fun setName(name: String) {
        _name = name
    }

    fun setAmount(amount: Double) {
        _amount = amount
    }

    fun setTargetDate(targetDate: Long) {
        _targetDate = targetDate
    }

    fun checkSavable() {
        _isSavable.value = _name.isNotEmpty() && _amount > 0
    }

    fun saveGoal(id : Long?, accountId: Long, targetDate: Long, colorId: Int) {
        val goal = Goal(
            id = id ?: 0,
            accountId = accountId,
            name = _name,
            amount = _amount,
            targetDate = targetDate,
            colorId = colorId
        )
        viewModelScope.launch(ioDispatcher) {
            if(id == null) {
                goalRepository.insertGoal(goal)
            } else {
                goalRepository.updateGoal(goal)
            }
        }
    }
}