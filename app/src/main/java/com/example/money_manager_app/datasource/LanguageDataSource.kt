package com.example.money_manager_app.datasource

import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.LanguageModel

object LanguageDataSource {
    fun getLanguageList() = listOf(
        LanguageModel(R.drawable.ic_english, "English", "en", true),
        LanguageModel(R.drawable.ic_vietnam, "Vietnam", "vi", false),
        LanguageModel(R.drawable.china_flag, "China", "zh", false),
        LanguageModel(R.drawable.india_flag, "India", "hi", false),
    )
}