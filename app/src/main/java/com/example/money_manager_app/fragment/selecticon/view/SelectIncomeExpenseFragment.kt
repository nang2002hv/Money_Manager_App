package com.example.money_manager_app.selecticon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.databinding.FragmentSelectIncomeExpenseBinding
import com.example.money_manager_app.fragment.add.view.expense.ExpenseViewModel
import com.example.money_manager_app.fragment.add.view.income.IncomeViewModel
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import com.example.money_manager_app.fragment.selecticon.Adapter.SelectIncomeExpenseAdapter

class SelectIncomeExpenseFragment : Fragment() {
    private var _binding: FragmentSelectIncomeExpenseBinding? = null
    private val binding get() = _binding!!
    private var typeExpense: String? = null
    private var selectIncomeExpenseAdapter = SelectIncomeExpenseAdapter(listOf(),::clickRadioButtonIconCategory)
    private val viewModel: AddViewModel by activityViewModels()
    private val incomeViewModel: IncomeViewModel by activityViewModels()
    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectIncomeExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showData()
        setupListeners()
        back()
    }

    private fun back(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.tvIncome.setOnClickListener {
            updateUIForIncome()
        }
        binding.tvExpense.setOnClickListener {
            updateUIForExpense()
        }
    }

    private fun updateUIForIncome() {
        binding.tvIncome.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBackgroundResource(R.drawable.customer_select_category_income)
        }
        binding.tvExpense.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setBackgroundResource(R.drawable.customer_select_category)
        }
        typeExpense = TransferType.Income.toString()
        selectIncomeExpenseAdapter.updateData(incomeViewModel.getCategoryListIncome())
    }

    private fun updateUIForExpense() {
        binding.tvExpense.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBackgroundResource(R.drawable.customer_select_category_expense)
        }
        binding.tvIncome.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setBackgroundResource(R.drawable.customer_select_category)
        }
        typeExpense = TransferType.Expense.toString()
        selectIncomeExpenseAdapter.updateData(expenseViewModel.getCategoryListExpense())
    }

    private fun initView() {
        typeExpense = arguments?.getString("type")
        when (typeExpense) {
            TransferType.Income.toString() -> updateUIForIncome()
            else -> updateUIForExpense()
        }
    }

    private fun clickRadioButtonIconCategory(categoryData: CategoryData.Category) {
        if (!categoryData.isCheck) {
            if (typeExpense == TransferType.Expense.toString()) {
                expenseViewModel.setOneCategoryExpense(categoryData)
            } else {
                incomeViewModel.setOneCategoryIcome(categoryData)
            }
            viewModel.setIdCategory(categoryData.id)
            val destination = if (typeExpense == TransferType.Expense.toString()) {
                viewModel.setPosition(1)
                R.id.addFragment
            } else {
                viewModel.setPosition(0)
                R.id.addFragment
            }
            findNavController().popBackStack()
        }
    }

    private fun showData() {
        binding.vpCategory.apply {
            adapter = selectIncomeExpenseAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}