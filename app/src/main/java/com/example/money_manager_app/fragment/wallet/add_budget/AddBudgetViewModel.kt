package com.example.money_manager_app.fragment.wallet.add_budget

import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.BudgetCategoryCrossRef
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.data.model.entity.enums.PeriodType
import com.example.money_manager_app.data.repository.BudgetRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddBudgetViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val budgetRepository: BudgetRepository
): BaseViewModel() {

    private val _budget = MutableStateFlow(Budget(0L, "", 0.0, 0, 0, 0, PeriodType.YEARLY, 0, 0,0))
    val budget get() = _budget

    fun insertBudget(budget: Budget, listCategory: List<Category>, listCategoryData: List<CategoryData.Category>) {
        val budgetCategoryCrossRefs = mutableListOf<BudgetCategoryCrossRef>()
        if(listCategoryData[0].isCheck){
            for(i in listCategory.indices){
                if(listCategory[i].type == CategoryType.EXPENSE){
                    budgetCategoryCrossRefs.add(BudgetCategoryCrossRef(budget.id,listCategory[i].id))
                }
            }
        } else {
            for(i in listCategoryData){
               if(i.isCheck){
                     budgetCategoryCrossRefs.add(BudgetCategoryCrossRef(budget.id,i.icon.toLong()))
               }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            budgetRepository.insertBudget(budget, budgetCategoryCrossRefs)
        }
    }

    fun editBudget(budget: Budget, listCategory: List<Category>,listCategoryData: List<CategoryData.Category>){
        val budgetCategoryCrossRefs = mutableListOf<BudgetCategoryCrossRef>()
        if(listCategoryData[0].isCheck){
            for(i in listCategory.indices){
                if(listCategory[i].type == CategoryType.EXPENSE){
                    budgetCategoryCrossRefs.add(BudgetCategoryCrossRef(budget.id,listCategory[i].id))
                }
            }
        } else {
            for(i in listCategoryData){
                if(i.isCheck){
                    budgetCategoryCrossRefs.add(BudgetCategoryCrossRef(budget.id,i.icon.toLong()))
                }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            budgetRepository.editBudget(budget,budgetCategoryCrossRefs)
        }
    }



    fun getDateFromDayOfWeek(dayOfWeek: String): String {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val dayMap = mapOf(
            "Sunday" to Calendar.SUNDAY,
            "Monday" to Calendar.MONDAY,
            "Tuesday" to Calendar.TUESDAY,
            "Wednesday" to Calendar.WEDNESDAY,
            "Thursday" to Calendar.THURSDAY,
            "Friday" to Calendar.FRIDAY,
            "Saturday" to Calendar.SATURDAY
        )
        val targetDay = dayMap[dayOfWeek] ?: return "Invalid Day"
        val diff = targetDay - today
        calendar.add(Calendar.DAY_OF_YEAR, diff)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getDateFromDayOfMonth(dayOfMonth: String): String {
        val dayNumber = dayOfMonth.replace(Regex("[^0-9]"), "")
        if (dayNumber.isEmpty()) return "Invalid Day"
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, dayNumber.toInt())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getMonthIndexFromName(monthName: String): Int {
        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.indexOf(monthName)
    }

    fun getDateFromDayOfMonth(dayOfMonth: String, monthName: String): String {
        val dayNumber = dayOfMonth.replace(Regex("[^0-9]"), "")
        if (dayNumber.isEmpty()) return "Invalid Day"

        val monthIndex = getMonthIndexFromName(monthName)
        if (monthIndex == -1) return "Invalid Month"

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, monthIndex)
        calendar.set(Calendar.DAY_OF_MONTH, dayNumber.toInt())
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (calendar.get(Calendar.DAY_OF_MONTH) > maxDay) {
            calendar.set(Calendar.DAY_OF_MONTH, maxDay)
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }



}