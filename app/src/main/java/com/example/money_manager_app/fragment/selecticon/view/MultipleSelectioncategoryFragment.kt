package com.example.money_manager_app.selecticon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragmentNotRequireViewModel
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.databinding.FragmentCategoryBinding
import com.example.money_manager_app.fragment.selecticon.Adapter.MultipleSelectionCategoryAdapter
import com.example.money_manager_app.fragment.selecticon.viewmodel.CategoryViewModel
import com.example.money_manager_app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MultipleSelectioncategoryFragment : BaseFragmentNotRequireViewModel<FragmentCategoryBinding>(R.layout.fragment_category){

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val categoryViewModel : CategoryViewModel by activityViewModels()
    private lateinit var multipleSelectionCategoryAdapter: MultipleSelectionCategoryAdapter
    private lateinit var listCategory: List<CategoryData.Category>

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mainViewModel.getCategories()
        setAdapter()
        observeData()
        back()
        save()
    }

    fun back(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    fun save(){
        binding.tvSave.setOnClickListener {
            categoryViewModel.setCategory(listCategory)
            findNavController().popBackStack()
        }
    }

    fun setAdapter(){
        listCategory = emptyList()
        multipleSelectionCategoryAdapter = MultipleSelectionCategoryAdapter(listOf(), ::onclick)
        binding.rvCategory.adapter = multipleSelectionCategoryAdapter
        binding.rvCategory.layoutManager = GridLayoutManager(requireContext(), 1)
    }

    fun onclick(category: CategoryData.Category, listCategory: List<CategoryData.Category>){
        if(category.icon.toInt() == 0){
            if(category.isCheck == false){
                for(i in listCategory.indices){
                    listCategory[i].isCheck = true
                }
            } else {
                for(i in listCategory.indices){
                    listCategory[i].isCheck = false
                }
            }
            this.listCategory = listCategory
            multipleSelectionCategoryAdapter.setListCategory(listCategory)
        }else{
            if(listCategory[0].isCheck == true){
                for(i in listCategory){
                    i.isCheck = false
                }
            }
            for(i in listCategory.indices){
                if(listCategory[i].icon.toInt() == category.icon.toInt()){
                    if(listCategory[i].isCheck) {
                        listCategory[i].isCheck = false
                    } else {
                        listCategory[i].isCheck = true
                    }
                }
            }
            this.listCategory = listCategory
            multipleSelectionCategoryAdapter.setListCategory(listCategory)
        }
    }

    fun observeData() {
        lifecycleScope.launch {
            mainViewModel.categories.collect {
                var list = getCategoryExpense(it)
                multipleSelectionCategoryAdapter.setListCategory(list)
            }
        }

        lifecycleScope.launch {
            categoryViewModel.categories.observe(viewLifecycleOwner){
                listCategory = it
                multipleSelectionCategoryAdapter.setListCategory(it)
            }
        }
    }

    fun getCategoryExpense(listCategory : List<Category>) : List<CategoryData.Category>{
        var list: MutableList<CategoryData.Category> = mutableListOf()
        list.add(CategoryData.Category(R.drawable.all_category, "All Category", "0", "Expense", false))
        for(category in listCategory){
            if(category.type == CategoryType.EXPENSE){
                list.add(CategoryData.Category(category.iconId, category.name.toString(), category.id.toString(), "Expense", false))
            }
        }
        return list.toList()
    }

}