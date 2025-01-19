package com.example.money_manager_app.fragment.create_account.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.Currency
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.data.repository.AccountRepository
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository
): BaseViewModel() {
    private var _name = ""
    private var _currency = MutableLiveData(Currency.USD)
    val currency: LiveData<Currency> get() = _currency
    private var _initAmount = 0.0
    private var _accountId : Long? = null

    private val _isEnterName = MutableLiveData<Boolean>()
    val isEnterName: LiveData<Boolean> get() = _isEnterName

    private val _currentPage = MutableLiveData(0)
    val currentPage get() = _currentPage

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun backPage() : Boolean {
        if (_currentPage.value == 0) return true
        _currentPage.value = _currentPage.value?.minus(1)
        return false
    }

    fun setName(name: String) {
        _name = name
        _currentPage.value = 1
    }

    fun getName(): String {
        return _name
    }

    fun setCurrency(currency: Currency) {
        _currency.value = currency
        _currentPage.value = 2
    }

    fun setInitAmount(initAmount: Double) {
        _initAmount = initAmount
        _currentPage.value = 3
    }

    fun checkEnterName(name: String) {
        _isEnterName.value = name.isNotEmpty()
    }

    fun getInitAmount(): Double {
        return _initAmount
    }
}
