package com.example.money_manager_app.fragment.wallet.add_wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val walletRepository: WalletRepository
) : BaseViewModel() {
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet : StateFlow<Wallet?> get() = _wallet

    private val _selectedIconId: MutableLiveData<Int> = MutableLiveData(R.drawable.wallet_1)
    val selectedIcon: MutableLiveData<Int> get() = _selectedIconId

    fun getWalletById(walletId: Long) {
        viewModelScope.launch(ioDispatcher) {
            walletRepository.getWalletById(walletId).collect {
                _wallet.value = it
            }
        }
    }

    fun addWallet(wallet: Wallet): Long {
        return if (wallet.name.isNotEmpty() && wallet.amount > 0) {
            var walletId: Long = -1
            viewModelScope.launch(ioDispatcher) {
                walletId = walletRepository.insertWallet(wallet)
            }
            walletId
        } else {
            -1L
        }
    }

    fun editWallet(wallet: Wallet) {
        viewModelScope.launch(ioDispatcher) {
            walletRepository.editWallet(wallet)
        }
    }

    fun setSelectedIconId(iconId: Int) {
        _selectedIconId.value = iconId
    }
}