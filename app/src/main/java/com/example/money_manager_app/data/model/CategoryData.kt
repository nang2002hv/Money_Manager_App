package com.example.money_manager_app.data.model

import android.content.Context
import android.util.Log
import com.example.money_manager_app.R

class CategoryData {
    data class CategoryList(val categorys: List<Category>)
    data class Category(val id: Int, var name: String, val icon: String, val type: String, var isCheck : Boolean = false)

    fun readCategoryIncome(content: Context) : List<Category>{
        var listCategory = mutableListOf<Category>()
        try {
            val listArrayIncome = content.resources.getStringArray(R.array.category_income)
            for(i in listArrayIncome.indices){
                listCategory.add(Category(i+1, listArrayIncome[i],"income_" + (i+1)  , "income"))
            }
        } catch (e: Exception) {
            }
        return listCategory
    }

    fun readCategoryExpense(content: Context) : List<Category>{
        var listCategory = mutableListOf<Category>()
        try {
            val listArrayExpense = content.resources.getStringArray(R.array.category_expense)
            for(i in listArrayExpense.indices){
                listCategory.add(Category(i+1, listArrayExpense[i],"expense_" + (i+1) , "expense"))
            }
        } catch (e: Exception) {
        }
        return listCategory
    }
}