package com.example.money_manager_app.fragment.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.CalendarRecord
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import com.example.money_manager_app.utils.groupRecordsByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel  @Inject constructor(
    private val transferRepository: TransferRepository,
    private val debtRepository: DebtRepository,
    private val debtTransactionRepository: DebtTransactionRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _detailTransaction : MutableStateFlow<List<Transaction>> = MutableStateFlow(emptyList())
    val detailTransaction : StateFlow<List<Transaction>> get() = _detailTransaction

    private var _calendarRecord : MutableStateFlow<List<CalendarRecord>> = MutableStateFlow(emptyList())
    val calendarRecord : StateFlow<List<CalendarRecord>> get() = _calendarRecord

    private fun setDetailDayTransaction(list : List<Transaction>) {
        _detailTransaction.value = list
    }

    fun getTransactionMonth(date : Date, idAccount : Long) {
        val firstDayOfMonth = getStartDayOfMonth(date)
        val lastDayOfMonth = getEndDayOfMonth(date)
        viewModelScope.launch(ioDispatcher) {
            val transfers = transferRepository.getTransferFromDayStartAndDayEnd(firstDayOfMonth.time, lastDayOfMonth.time, idAccount).first()
            val debts = debtRepository.getDebtByDayStartAndDayEnd(idAccount,firstDayOfMonth.time, lastDayOfMonth.time).first()
            val debtTransactions = debtTransactionRepository.getDebtTransactionFromDayStartAndDayEnd(idAccount, firstDayOfMonth.time, lastDayOfMonth.time).first()
            val list = mutableListOf<Transaction>()
            list.addAll(transfers)
            list.addAll(debts)
            list.addAll(debtTransactions)
            setDetailDayTransaction(list)


        }
    }

    fun setCalendarRecord(listTransaction : List<Transaction>){
        val listCalendarRecord = listTransaction.groupRecordsByDate()
        _calendarRecord.value = listCalendarRecord
    }



    private fun getStartDayOfMonth(date : Date) : Date {
        val calendar = getMidnightCalendar(date)
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.time
    }

    private fun getEndDayOfMonth(date : Date) : Date {
        val calendar = getMidnightCalendar(date)
        calendar.time = date
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDay)
        return calendar.time
    }

    private fun getMidnightCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }


}