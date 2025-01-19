package com.example.money_manager_app.fragment.add.view.income

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Environment
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.AddTransfer
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.data.repository.TransferRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
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
class IncomeViewModel @Inject constructor(
    private val repository: TransferRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
)  : BaseViewModel() {

    private val _categoryListIncome = MutableStateFlow<List<CategoryData.Category>>(emptyList())

    private val _addTransfer = MutableStateFlow(AddTransfer())

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> get() = _selectedDate

    private val _selectedTime = MutableStateFlow("")
    val selectedTime: StateFlow<String> get() = _selectedTime

    private val _currentDateTime = MutableStateFlow(Pair("", ""))
    val currentDateTime: StateFlow<Pair<String, String>> get() = _currentDateTime

    private val _imageUri = MutableStateFlow<Bitmap?>(null)
    val imageUri: StateFlow<Bitmap?> get() = _imageUri

    private var _categoryNameIncome = MutableStateFlow(Pair("", 0))
    val categoryNameIncome: StateFlow<Pair<String, Int>> get() = _categoryNameIncome

    private var _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> get() = _amount

    private var _descriptor = MutableStateFlow("")
    val descriptor: StateFlow<String> get() = _descriptor

    private var _fee = MutableStateFlow(0.0)
    val fee: StateFlow<Double> get() = _fee

    private var _id = MutableStateFlow<Long>(0)
    val id: StateFlow<Long> get() = _id

    private var _momo = MutableStateFlow("")
    val momo: StateFlow<String> get() = _momo

    private var _fromWallet : MutableStateFlow<List<Wallet>> = MutableStateFlow(emptyList())
    val fromWallet: StateFlow<List<Wallet>> get() = _fromWallet

    private var _toWallet : MutableStateFlow<List<Wallet>> = MutableStateFlow(emptyList())
    val toWallet: StateFlow<List<Wallet>> get() = _toWallet

    private var _oldwallet = MutableStateFlow(Wallet(0,0.0,0, WalletType.GENERAL,"",0,0,true))

    private var _oldAmount = MutableStateFlow(0.0)

    fun setOldAmount(amount: Double){
        _oldAmount.value = amount
    }

    fun setOldWallet(wallet: Wallet){
        _oldwallet.value = wallet
    }


    fun setFromWallet (list: List<Wallet>){
        _fromWallet.value = list
    }

    fun setAmount(amount: Double) {
        _amount.value = amount
    }

    fun getAmount(): Double {
        return amount.value
    }

    fun setDescriptor(descriptor: String) {
        _descriptor.value = descriptor
    }

    fun getDescriptor(): String {
        return descriptor.value
    }


    fun setMomo(momo: String) {
        _momo.value = momo
    }

    fun getMomo(): String {
        return momo.value
    }

    fun setCategoryNameIncome(pair : Pair<String, Int>) {
        _categoryNameIncome.value = pair
    }

    fun getCategoryNameIncome(): Pair<String, Int> {
        return categoryNameIncome.value
    }

    fun setId(id: Long) {
        _id.value = id
    }

    fun getId(): Long {
        return id.value
    }

    fun setCategoryListIncome(list: List<CategoryData.Category>) {
        _categoryListIncome.value = list
    }

    fun setOneCategoryIcome(category: CategoryData.Category) {
        val updatedList = _categoryListIncome.value.map {
            if (it.id == category.id) {
                it.copy(isCheck = true)
            } else {
                it.copy(isCheck = false)
            }
        }
        _categoryListIncome.value = updatedList
    }

    fun getCategoryListIncome(): List<CategoryData.Category> {
        return _categoryListIncome.value
    }

    fun setBitmap(bitmap: Bitmap) {
        _imageUri.value = bitmap
    }

    fun getBitmap(): Bitmap? {
        return _imageUri.value
    }

    fun setDateAndTime(date: String, time: String) {
        _currentDateTime.value = Pair(date, time)
    }

    fun getDateTime(): Pair<String, String> {
        return currentDateTime.value
    }

    fun saveDrawableToAppStorage(context: Context, bitmap : Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        val output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val directory = File(output, "image")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val filename = "IMG_${System.currentTimeMillis()}.png"
        val file = File(directory, filename)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }



    fun showDatePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            _selectedDate.value = selectedDate
            _currentDateTime.value = Pair(selectedDate, _currentDateTime.value.second)
        }, year, month, day)
        datePickerDialog.show()
    }

    fun showTimePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val selectedTime = "$selectedHour:$selectedMinute"
            _selectedTime.value = selectedTime
            _currentDateTime.value = Pair(_currentDateTime.value.first, selectedTime)
        }, hour, minute, true)
        timePickerDialog.show()
    }

    fun updateDateTime() {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        _currentDateTime.value = Pair(dateFormat.format(currentDate), timeFormat.format(currentDate))
    }

    fun saveIncomeAndExpense(transfer: Transfer) {
        viewModelScope.launch(ioDispatcher) {
            if (transfer.amount > 0) {
                repository.insertTransferDetail(
                    transfer
                )

            }
        }
    }

    fun editIncomeAndExpense(transfer: Transfer) {
        viewModelScope.launch(ioDispatcher) {
            if (transfer.amount > 0) {
                repository.editTransferDetail(
                    transfer
                )
            }
        }
    }

    public override fun onCleared(){
        super.onCleared()
        _id.value = 0
        _imageUri.value = null
        _selectedDate.value = ""
        _selectedTime.value = ""
        _currentDateTime.value = Pair("", "")
        _categoryNameIncome.value = Pair("", 0)
        _amount.value = 0.0
        _descriptor.value = ""
        _fee.value = 0.0
        _momo.value = ""
        _addTransfer.value = AddTransfer()
        _toWallet.value = emptyList()
        _fromWallet.value = emptyList()
    }

}
