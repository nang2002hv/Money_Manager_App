package com.example.money_manager_app.fragment.add.viewmodel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.AddTransfer
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.data.repository.WalletRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
)  : BaseViewModel() {

    private val _position = MutableStateFlow(-1)
    val position: StateFlow<Int> = _position

    private val _idCategory = MutableStateFlow(0)
    val idCategory: StateFlow<Int> = _idCategory

    private val _checkEdit = MutableStateFlow(false)
    val checkEdit: StateFlow<Boolean> = _checkEdit

    fun setCheckEdit(check: Boolean){
        _checkEdit.value = check
    }
    fun getCheckEdit(): Boolean {
        return checkEdit.value
    }


    fun setIdCategory(id: Int){
        _idCategory.value = id
    }

    fun getIdCategory(): Int {
        return idCategory.value
    }

    fun setPosition(position: Int){
        _position.value = position
    }

    fun getPosition(): Int {
        return position.value
    }

    public override fun onCleared(){
        super.onCleared()
        _position.value = -1
        _idCategory.value = 0
        _checkEdit.value = false
    }

}
