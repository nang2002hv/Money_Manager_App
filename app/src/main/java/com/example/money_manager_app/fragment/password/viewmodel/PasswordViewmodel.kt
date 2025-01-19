package com.example.money_manager_app.fragment.password.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.pref.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordViewmodel @Inject constructor(
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    private val _numbersEnter = MutableLiveData<MutableMap<Int, String>>()
    val numbersEnter: LiveData<MutableMap<Int, String>> = _numbersEnter

    private val _currentCursor = MutableLiveData<Int>()
    val currentCursor: LiveData<Int> = _currentCursor

    private val _isPasswordCorrect = MutableLiveData<Boolean>()
    val isPasswordCorrect: LiveData<Boolean> = _isPasswordCorrect

    private val _currentPasswordType = MutableLiveData<PasswordType>()
    val currentPasswordType: LiveData<PasswordType> = _currentPasswordType

    private val _inputPasswordCount = MutableLiveData<Int>(0)
    val inputPasswordCount: LiveData<Int> get() = _inputPasswordCount

    init {
        _numbersEnter.postValue(
            mutableMapOf(
                0 to "",
                1 to "",
                2 to "",
                3 to "",
                4 to "",
                5 to ""
            )
        )

        _currentCursor.postValue(0)

        if (appPreferences.getPassword().isEmpty()) {
            _currentPasswordType.postValue(PasswordType.CREATE)
        } else {
            _currentPasswordType.postValue(PasswordType.CHECK)
        }
    }

    fun addNumber(number: String) {
        val numbers = _numbersEnter.value ?: return
        val cursor = _currentCursor.value ?: return
        val type = _currentPasswordType.value ?: return


        numbers[cursor] = number

        _numbersEnter.postValue(numbers)

        if (cursor == 5) {
            when (type) {
                PasswordType.CREATE -> {
                    createPassword()
                    _currentPasswordType.postValue(PasswordType.CONFIRM)
                }

                PasswordType.CONFIRM -> checkPassword()
                PasswordType.CHECK -> checkPassword()
            }
            return
        }

        _currentCursor.postValue(cursor + 1)
    }

    private fun checkPassword() {
        val numbers = _numbersEnter.value ?: return

        // Only check password if the user has actually entered something
        if (numbers.values.any { it.isNotEmpty() }) {
            val password = numbers.values.joinToString("")
            val passwordConfirm = appPreferences.getPassword()

            var entries = inputPasswordCount.value ?: 0
            entries = entries.coerceAtLeast(0) // Ensure the count is non-negative.

            if (password != passwordConfirm) (
                _inputPasswordCount.postValue(entries + 1)
            )

            _isPasswordCorrect.postValue(password == passwordConfirm)
        }
    }


    private fun createPassword() {
        val numbers = _numbersEnter.value ?: return

        val password = numbers.values.joinToString("")
        appPreferences.setPassword(password)
    }

    fun deleteNumber() {
        val numbers = _numbersEnter.value ?: return
        val cursor = _currentCursor.value ?: return

        if (cursor == 0) return

        numbers[cursor - 1] = ""

        _numbersEnter.postValue(numbers)
        _currentCursor.postValue(cursor - 1)
    }

    fun reset() {
        _numbersEnter.postValue(
            mutableMapOf(
                0 to "",
                1 to "",
                2 to "",
                3 to "",
                4 to "",
                5 to ""
            )
        )

        _currentCursor.postValue(0)
    }

}

enum class PasswordType {
    CREATE,
    CONFIRM,
    CHECK
}