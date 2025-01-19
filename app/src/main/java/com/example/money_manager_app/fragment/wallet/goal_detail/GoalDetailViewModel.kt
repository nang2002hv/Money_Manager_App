package com.example.money_manager_app.fragment.wallet.goal_detail

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.TransactionListItem
import com.example.money_manager_app.data.model.entity.GoalDetail
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.repository.GoalRepository
import com.example.money_manager_app.utils.groupTransactionsByDate
import com.example.money_manager_app.utils.toFormattedDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : BaseViewModel() {
    private val _goal = MutableStateFlow<GoalDetail?>(null)
    val goal: StateFlow<GoalDetail?> get() = _goal

    private val _goalDetailItem = MutableStateFlow<GoalDetailItem?>(null)
    val goalDetailItem: StateFlow<GoalDetailItem?> get() = _goalDetailItem

    fun getGoalDetail(goalId: Long) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).collect {
                _goal.value = it
                it?.let {
                    _goalDetailItem.value = convertGoalDetailToGoalDetailItem(it)
                }
            }
        }
    }

    fun deleteGoal() {
        viewModelScope.launch {
            goalRepository.deleteGoal(_goal.value!!.goal)
        }
    }

    private fun convertGoalDetailToGoalDetailItem(goalDetail: GoalDetail): GoalDetailItem {
        val saveAmount = goalDetail.transactions.filter { it.type == GoalInputType.DEPOSIT }
            .sumOf { it.amount } - goalDetail.transactions.filter { it.type == GoalInputType.WITHDRAW }
            .sumOf { it.amount }
        val remainAmount = goalDetail.goal.amount - saveAmount
        val goalDate = goalDetail.goal.targetDate.toFormattedDateString()
        val daysLeft = calculateDaysLeft(goalDetail.goal.targetDate)
        val groupedTransactions = goalDetail.transactions.groupTransactionsByDate()
        return GoalDetailItem(
            title = goalDetail.goal.name,
            saveAmount = saveAmount,
            remainAmount = remainAmount,
            amountGoal = goalDetail.goal.amount,
            goalDate = goalDate,
            daysLeft = daysLeft,
            colorId = goalDetail.goal.colorId,
            transactions = groupedTransactions
        )
    }

    private fun calculateDaysLeft(targetDate: Long): String {
        val currentDate = Calendar.getInstance().timeInMillis
        val diffInMillis = targetDate - currentDate
        val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        return daysLeft.toString()
    }
}

data class GoalDetailItem(
    val title: String,
    val saveAmount: Double,
    val remainAmount: Double,
    val amountGoal: Double,
    val goalDate: String,
    val daysLeft: String,
    val transactions: List<TransactionListItem>,
    val progress: Int = ((saveAmount / amountGoal) * 100).toInt(),
    val colorId: Int = R.color.color_1
)