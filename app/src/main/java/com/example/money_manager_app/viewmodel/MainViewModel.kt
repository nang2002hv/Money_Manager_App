package com.example.money_manager_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.AccountWithWalletItem
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.data.repository.AccountRepository
import com.example.money_manager_app.data.repository.CategoryRepository
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import com.example.money_manager_app.pref.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(AppDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
    private val appPreferences: AppPreferences,
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository
) : BaseViewModel() {
    private val _accounts: MutableStateFlow<List<AccountWithWalletItem>> = MutableStateFlow(emptyList())
    val accounts: StateFlow<List<AccountWithWalletItem>> get() = _accounts

    private val _currentAccount: MutableStateFlow<AccountWithWalletItem?> = MutableStateFlow(null)
    val currentAccount: StateFlow<AccountWithWalletItem?> get() = _currentAccount

    private val _currentBalance: MutableLiveData<Double> = MutableLiveData(0.0)
    val currentBalance: LiveData<Double> get() = _currentBalance

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    private val _hiddenBalance = MutableLiveData<Boolean>(false)
    val hiddenBalance : LiveData<Boolean> get() = _hiddenBalance

    private val _currentLanguage = MutableLiveData<String>()
    val currentLanguage: LiveData<String> get() = _currentLanguage

    init {
        fetchHiddenState()
        fetchAccountsAndSetCurrentAccount()
        observeCurrentAccount()
        getLanguage()
    }

    private fun fetchHiddenState(){
        _hiddenBalance.postValue(appPreferences.getHiddenBalance())
    }

    fun toggleHiddenBalance() {
        val currentValue = _hiddenBalance.value ?: false
        val newValue = !currentValue
        appPreferences.setHiddenBalance(newValue)
        _hiddenBalance.postValue(newValue) // Update LiveData
    }


    fun getAccount() {
        viewModelScope.launch {
            accountRepository.getAccount().collect {
                _accounts.value = it
            }
        }
    }

    fun getCategories(){
        viewModelScope.launch {
            categoryRepository.getCategory().collect {
                _categories.value = it
            }
        }
    }

    private fun getLanguage() {
        viewModelScope.launch {
            _currentLanguage.postValue(appPreferences.getLanguage())
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            appPreferences.setLanguage(language)
            _currentLanguage.postValue(language)
        }
    }

    fun insertCategory(listCategory: List<Category>){
        viewModelScope.launch {
            categoryRepository.insertCategory(listCategory)
        }
    }

    fun setCurrentAccount(account: AccountWithWalletItem) {
        _currentAccount.value = account
        appPreferences.setCurrentAccount(account.account.id)
    }

    fun insertAccount(account: Account, initAmount: Double) {
        viewModelScope.launch(ioDispatcher) {
            if (account.nameAccount.isNotEmpty()) {
                val accountId = accountRepository.insertAccount(account)
                walletRepository.insertWallet(
                    Wallet(
                        accountId = accountId,
                        amount = initAmount,
                        walletType = WalletType.GENERAL,
                        name = "General",
                        isExcluded = false
                    )
                )
                fetchCurrentAccountIfNeeded(accountId)
            }
        }
    }

    private fun fetchAccountsAndSetCurrentAccount() {
        viewModelScope.launch(ioDispatcher) {
            accountRepository.getAccount().collect { accountList ->
                _accounts.value = accountList

                // Set the current account only if it is not already set
                if (
//                    _currentAccount.value == null &&
                    accountList.isNotEmpty()) {
                    val savedAccountId = appPreferences.getCurrentAccount()
                    val current = accountList.find { it.account.id == savedAccountId } ?: accountList.first()
                    _currentAccount.value = current
                    observeBalance()
                }

            }
        }
    }

    private fun observeBalance() {
        viewModelScope.launch(defaultDispatcher) {
            currentAccount.collect {
                var balance = 0.0
                it?.walletItems?.let { walletItems ->
                    for (walletItem in walletItems) {
                        if (walletItem.wallet.walletType == WalletType.GENERAL && walletItem.wallet.isExcluded == false) {
                            balance += walletItem.endingAmount
                        }
                    }
                    _currentBalance.postValue(balance)
                }
            }
        }
    }

    private fun fetchCurrentAccountIfNeeded(accountId: Long) {
        viewModelScope.launch(ioDispatcher) {
            // Check if current account is already set
//            if (_currentAccount.value == null) {
            accountRepository.getAccountById(accountId).collect { fetchedAccount ->
//                    _currentAccount.value = fetchedAccount
                setCurrentAccount(fetchedAccount)
            }
//            }
        }
    }

    private fun observeCurrentAccount() {
        viewModelScope.launch {
            _currentAccount.collect { account ->
                account?.let {
                    getCategoriesByAccountId(it.account.id)
                }
            }
        }
    }

    private fun getCategoriesByAccountId(accountId: Long){
        viewModelScope.launch(ioDispatcher) {
            categoryRepository.getCategoriesByAccountId(accountId).collect {
                _categories.value = it
            }
        }
    }
}