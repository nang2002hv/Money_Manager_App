package com.example.money_manager_app.fragment.add.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.databinding.FragmentAddBinding
import com.example.money_manager_app.fragment.add.view.expense.ExpenseViewModel
import com.example.money_manager_app.fragment.add.view.income.IncomeViewModel
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import com.example.moneymanager.ui.add.adapter.AddPagerAdapter
import com.example.moneymanager.ui.add.adapter.AddTransferInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : BaseFragment<FragmentAddBinding, AddViewModel>(R.layout.fragment_add) {

    override fun getVM(): AddViewModel {
        val vm: AddViewModel by activityViewModels()
        return vm
    }

    private val expenseViewModel : ExpenseViewModel by activityViewModels()
    private val incomeViewModel : IncomeViewModel by activityViewModels()
    private lateinit var adapter: AddPagerAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        back()

    }

    fun setAdapter(){
        adapter = AddPagerAdapter(this)
        binding.vpAdd.adapter = adapter
        binding.vpAdd.setPageTransformer(DepthPageTransformer())
        binding.vpAdd.post(this::setTab)
        binding.vpAdd.post(this::tabLayout)
        binding.vpAdd.post(this::saveTransfer)
    }

    fun back(){
        binding.ivBack.setOnClickListener {
            onBack()
            onClear()
        }
    }

    override fun onBack() {
        super.onBack()
        appNavigation.navigateUp()
    }


    fun onClear() {
        expenseViewModel.onCleared()
        incomeViewModel.onCleared()
        getVM().onCleared()
    }


    fun setTab() {
        var position = if(getVM().getPosition() == -1) 1 else getVM().getPosition()
        binding.vpAdd.currentItem = position
        resetTabStyles()
        when (position) {
            0 -> setActiveTab(binding.tvIncome, R.drawable.customer_select_category_income)
            1 -> setActiveTab(binding.tvAddExpense, R.drawable.customer_select_category_expense)
            2 -> setActiveTab(binding.tvTransfer, R.drawable.custom_select_tranfer)
        }
        setCategory(position)
    }

    fun setCategory(position: Int) {
        val bundle = arguments
        val idCategory = if(getVM().getIdCategory() == 0) 0 else getVM().getIdCategory()

        if (idCategory != 0) {
            when (position) {
                0 -> {
                    val listCategoryIncome = resources.getStringArray(R.array.category_income)
                    val pair : Pair<String, Int> = Pair(listCategoryIncome[idCategory - 1], idCategory)
                    incomeViewModel.setCategoryNameIncome(pair)
                }
                1 -> {
                    val listCategoryExpense = resources.getStringArray(R.array.category_expense)
                    val pair : Pair<String, Int> = Pair(listCategoryExpense[idCategory - 1], idCategory)
                    expenseViewModel.setCategoryNameExpense(pair)
                }
            }
        }
    }



    fun tabLayout() {
        val tabClickListener = View.OnClickListener { view ->
            resetTabStyles()
            when (view.id) {
                R.id.tv_income -> {
                    setActiveTab(binding.tvIncome, R.drawable.customer_select_category_income)
                    binding.vpAdd.currentItem = 0
                }
                R.id.tv_add_expense -> {
                    setActiveTab(binding.tvAddExpense, R.drawable.customer_select_category_expense)
                    binding.vpAdd.currentItem = 1
                }
                R.id.tv_transfer -> {
                    setActiveTab(binding.tvTransfer, R.drawable.custom_select_tranfer)
                    binding.vpAdd.currentItem = 2
                }
            }
        }

        binding.tvIncome.setOnClickListener(tabClickListener)
        binding.tvAddExpense.setOnClickListener(tabClickListener)
        binding.tvTransfer.setOnClickListener(tabClickListener)
    }

    private fun resetTabStyles() {
        binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvIncome.setBackgroundResource(0)
        binding.tvAddExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvAddExpense.setBackgroundResource(0)
        binding.tvTransfer.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvTransfer.setBackgroundResource(0)
    }

    private fun setActiveTab(tab: TextView, backgroundRes: Int) {
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        tab.setBackgroundResource(backgroundRes)
    }

    fun saveTransfer() {
        binding.tvSave.setOnClickListener {
            if(getVM().getCheckEdit()){
                val currentFragment = childFragmentManager.findFragmentByTag("f" + binding.vpAdd.currentItem)
                if (currentFragment is AddTransferInterface) {
                    when (binding.vpAdd.currentItem) {
                        0 -> currentFragment.onEdit()
                        1 -> currentFragment.onEdit()
                        2 -> currentFragment.onEdit()
                    }
                }
            } else {
                val currentFragment = childFragmentManager.findFragmentByTag("f" + binding.vpAdd.currentItem)
                if (currentFragment is AddTransferInterface) {
                    when (binding.vpAdd.currentItem) {
                        0 -> currentFragment.onSaveIncome()
                        1 -> currentFragment.onSaveExpense()
                        2 -> currentFragment.onSaveTransfer()
                    }
                }
            }
        }
    }
}

class DepthPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            when {
                position < -1 -> alpha = 0f
                position <= 0 -> {
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> {
                    alpha = 1 - position
                    translationX = view.width * -position
                    translationZ = -1f
                    scaleX = 0.75f + (1 - 0.75f) * (1 - Math.abs(position))
                    scaleY = 0.75f + (1 - 0.75f) * (1 - Math.abs(position))
                }
                else -> alpha = 0f
            }
        }
    }
}