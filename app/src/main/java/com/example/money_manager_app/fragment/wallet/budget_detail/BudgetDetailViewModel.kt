package com.example.money_manager_app.fragment.wallet.budget_detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.CategoryTransactionDetail
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.BudgetWithCategory
import com.example.money_manager_app.data.model.entity.CategoryWithTransfer
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.repository.BudgetRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val transferRepository: TransferRepository,
    private val budgetRepository: BudgetRepository
) : BaseViewModel() {

    private val _listCategoryWithTransfer = MutableStateFlow<List<CategoryWithTransfer>>(listOf())
    val listCategoryWithTransfer = _listCategoryWithTransfer

    private val _listTransfer = MutableStateFlow<List<Transfer>>(listOf())
    val listTransfer = _listTransfer

    private val _budgetsWithCategory = MutableStateFlow<List<BudgetWithCategory>>(emptyList())
    val budgetsWithCategory: StateFlow<List<BudgetWithCategory>> get() = _budgetsWithCategory

    fun getBudgets(accountId: Long) {
        viewModelScope.launch {
            budgetRepository.getBudgetsByAccountId(accountId).collect {
                _budgetsWithCategory.value = it
            }
        }
    }

    fun getCategoryWithTransfer(accountId: Long, categoryId: Long ,startDate: Long, endDate: Long) : List<Transfer> {
        return transferRepository.getTransferWithCategoryByAccountId(accountId, categoryId, startDate, endDate)
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch(ioDispatcher) {
            budgetRepository.deleteBudget(budget.id)
        }
    }

    fun getCategoryWithTransfer(budgetWithCategory: BudgetWithCategory) {
        viewModelScope.launch(ioDispatcher) {
            var listcategoryWithTransfer = mutableListOf<CategoryWithTransfer>()
            for(i in budgetWithCategory.categories){
                var listTransfer = getCategoryWithTransfer(budgetWithCategory.budget.accountId,
                    i.id, budgetWithCategory.budget.startDate,
                    budgetWithCategory.budget.endDate)
                listcategoryWithTransfer.add(CategoryWithTransfer(i, listTransfer))
            }
            _listCategoryWithTransfer.value = listcategoryWithTransfer
        }
    }

    fun toCategoryTransactionDetail(listCategoryWithTransfer: List<CategoryWithTransfer>): List<CategoryTransactionDetail> {
        var listCategoryTransactionDetail = mutableListOf<CategoryTransactionDetail>()
        for(i in listCategoryWithTransfer){
            if(i.transfers.isNotEmpty()){
                var total = 0.0
                var name = i.category.name
                var totalCategoryTransaction = 0
                for(j in i.transfers){
                    total += j.amount
                    totalCategoryTransaction++
                }
                listCategoryTransactionDetail.add(CategoryTransactionDetail(name = name, totalAmount = total,
                    totalCategoryTransaction = totalCategoryTransaction,
                    listTransfer = i.transfers))
            }

        }
        return listCategoryTransactionDetail
    }

}