package com.example.money_manager_app.fragment.selecticon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.money_manager_app.base.BaseViewModel
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.repository.CategoryRepository
import com.example.money_manager_app.di.AppDispatchers
import com.example.money_manager_app.di.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val categoryRepository: CategoryRepository
) : BaseViewModel() {

    private val _categories = MutableLiveData<List<CategoryData.Category>>()
    val categories: LiveData<List<CategoryData.Category>> = _categories

    private val _listCategory = MutableStateFlow<List<Category>>(listOf())
    val listCategory = _listCategory

    fun getCategory() {
        viewModelScope.launch() {
            categoryRepository.getCategory().collect {
                _listCategory.value = it
            }
        }
    }

    fun setCategory(category: List<CategoryData.Category>) {
        _categories.value = category
    }

}