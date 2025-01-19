package com.example.money_manager_app.fragment.search.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.data.repository.DebtRepository
import com.example.money_manager_app.data.repository.DebtTransactionRepository
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import com.example.money_manager_app.utils.toDateTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val debtTransactionRepository: DebtTransactionRepository,
    private val transferRepository: TransferRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _walletSearch = MutableStateFlow<Wallet>(Wallet(0,0.0,0,WalletType.GENERAL,"",0,0,true,))
    val walletSearch: StateFlow<Wallet> get() = _walletSearch

    private val _categorySearch = MutableStateFlow<CategoryData.Category>(CategoryData.Category(0,"",",","",true))
    val categorySearch: StateFlow<CategoryData.Category> get() = _categorySearch

    private val _minAmount = MutableStateFlow<Double?>(null)
    val minAmount: StateFlow<Double?> get() = _minAmount

    private val _maxAmount = MutableStateFlow<Double?>(null)
    val maxAmount: StateFlow<Double?> get() = _maxAmount

    private val _startDate = MutableStateFlow<String?>(null)
    val startDate: StateFlow<String?> get() = _startDate

    private val _endDate = MutableStateFlow<String?>(null)
    val endDate: StateFlow<String?> get() = _endDate

    private val _description = MutableStateFlow<String?>(null)
    val description: StateFlow<String?> get() = _description

    private val _listTransaction = MutableStateFlow<List<Transaction>>(emptyList())
    val listTransaction: StateFlow<List<Transaction>> get() = _listTransaction

    private val _idCategory = MutableStateFlow<Long>(0L)
    val idCategory: StateFlow<Long> get() = _idCategory

    fun setIdCategory(id: Long) {
        _idCategory.value = id
    }

    fun setListTransaction(list: List<Transaction>) {
        _listTransaction.value = list
    }

    fun searchByDateAndAmountAndDesAndCategoryAndWallet(idAcount : Long) {
        viewModelScope.launch(ioDispatcher) {
            val list = mutableListOf<Transaction>()
            val wallet = walletSearch.value.id.takeIf { it != 0L }
            val category = _idCategory.value.takeIf { it != 0L }
            val minAmount = minAmount.value.takeIf { it != null && it != 0.0 }
            val maxAmount = maxAmount.value.takeIf { it != null && it != 0.0 }
            val startDate = startDate.value?.takeIf { it.isNotEmpty() }?.toDateTimestamp()
            val endDate = endDate.value?.takeIf { it.isNotEmpty() }?.toDateTimestamp()
            val description = description.value.takeIf { !it.isNullOrEmpty() }

            list.addAll(debtRepository.searchByDateAndAmountAndDesAndCategoryAndWallet(startDate, endDate, minAmount, maxAmount, description, category, wallet, idAcount))
            list.addAll(debtTransactionRepository.searchByDateAndAmountAndDesAndCategoryAndWallet(startDate, endDate, minAmount, maxAmount, category, wallet, idAcount))
            list.addAll(transferRepository.searchByDateAndAmountAndDesAndCategoryAndWallet(startDate, endDate, minAmount, maxAmount, description, wallet, category, idAcount))
            setListTransaction(list)
        }
    }

    fun setWalletSearch(wallet: Wallet) {
        _walletSearch.value = wallet
    }

    fun setCategorySearch(category: CategoryData.Category) {
        _categorySearch.value = category
    }

    fun setMinAmount(minAmount: Double) {
        _minAmount.value = minAmount
    }

    fun setMaxAmount(maxAmount: Double) {
        _maxAmount.value = maxAmount
    }

    fun setStartDate(startDate: String) {
        _startDate.value = startDate
    }

    fun setEndDate(endDate: String) {
        _endDate.value = endDate
    }

    fun setDescription(description: String) {
        _description.value = description
    }


    fun clearFilter() {
        _walletSearch.value = Wallet(0,0.0,0,WalletType.GENERAL,"",0,0,true,)
        _categorySearch.value = CategoryData.Category(0,"",",","",true)
        _minAmount.value = null
        _maxAmount.value = null
        _startDate.value = null
        _endDate.value = null
        _description.value = null
        _listTransaction.value = emptyList()
    }
}