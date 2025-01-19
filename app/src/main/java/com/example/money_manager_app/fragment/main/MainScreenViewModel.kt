package com.example.money_manager_app.fragment.main

import com.example.money_manager_app.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel : BaseViewModel() {
    private val _currentFragmentId = MutableStateFlow(0)
    val currentFragmentId: StateFlow<Int> get() = _currentFragmentId

    fun setCurrentFragmentId(id: Int) {
        _currentFragmentId.value = id
    }
}