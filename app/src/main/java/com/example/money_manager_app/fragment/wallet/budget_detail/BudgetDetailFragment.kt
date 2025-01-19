package com.example.money_manager_app.fragment.wallet.budget_detail

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.CategoryTransactionDetail
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.CategoryWithTransfer
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import com.example.money_manager_app.databinding.AlertDialogBinding
import com.example.money_manager_app.databinding.FragmentBudgetDetailBinding
import com.example.money_manager_app.fragment.wallet.adapter.CategoryTransactionAdapter
import com.example.money_manager_app.fragment.wallet.add_budget.AddBudgetViewModel
import com.example.money_manager_app.fragment.selecticon.viewmodel.CategoryViewModel
import com.example.money_manager_app.utils.toFormattedDateString
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class BudgetDetailFragment : BaseFragment<FragmentBudgetDetailBinding, BudgetDetailViewModel>(R.layout.fragment_budget_detail) {

    override fun getVM(): BudgetDetailViewModel {
        val viewModel: BudgetDetailViewModel by activityViewModels()
        return viewModel
    }

    var currencySymbol = ""
    private var budget: Budget? = null
    private var category: String = ""
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var categoryTransactionAdapter: CategoryTransactionAdapter

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initData(savedInstanceState)
        setAdapter()
        observeData()
        setupBackButton()
        deleteButton()
        editButton()
    }

    private fun editButton() {
        binding.editButton.setOnClickListener {
            appNavigation.openBudgetDetailToAddBudgetScreen(Bundle().apply {
                putParcelable("budget", budget)
                putString("category", category)
            })
        }
    }

    private fun deleteButton() {
        binding.deleteButton.setOnClickListener {
            if(budget != null){
                val AlertDialogBinding = AlertDialogBinding.inflate(layoutInflater)

                var alert = AlertDialog.Builder(requireContext()).create()
                alert.setView(AlertDialogBinding.root)
                alert.setCancelable(true)
                AlertDialogBinding.deleteImageView.setOnClickListener {
                    getVM().deleteBudget(budget!!)
                    alert.dismiss()
                    findNavController().popBackStack()
                }
                AlertDialogBinding.cannelImageView.setOnClickListener {
                    alert.dismiss()
                }
                alert.show()
            }
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter() {
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        currencySymbol = getString(currentCurrency.symbolRes)
        categoryTransactionAdapter = CategoryTransactionAdapter(listOf(), currencySymbol, ::onTransactionItemClick)
        binding.transactionRv.adapter = categoryTransactionAdapter
        binding.transactionRv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        lifecycleScope.launch {
            getVM().listCategoryWithTransfer.collect {
                showData(it)
            }
        }

        lifecycleScope.launch {
            getVM().budgetsWithCategory.collect {
                val budgetWithCategory = it.find { it.budget.id == budget!!.id }
                budgetWithCategory?.let { bwc ->
                    binding.budgetLabel.text = "${getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)}${bwc.budget.amount}"
                    category = bwc.categories.joinToString(", ") { it.name }
                    if (bwc.categories.size == categoryViewModel.listCategory.value.filter { it.type == CategoryType.EXPENSE }.size) {
                        category = requireContext().getString(R.string.all_category)
                    }
                    binding.nameLabel.text = bwc.budget.name
                    binding.remainTitleLabel.text = if (bwc.budget.spent > bwc.budget.amount) requireContext().getString(R.string.overspent) else requireContext().getString(R.string.left)
                    binding.remainLabel.text = "${currencySymbol}${bwc.budget.amount - bwc.budget.spent}"
                    binding.spentLabel.text = "${currencySymbol}${bwc.budget.spent}"
                    binding.categoryLabel.text = category
                    getVM().getCategoryWithTransfer(bwc)
                }
            }
        }
    }

    private fun onTransactionItemClick(categoryTransaction : CategoryTransactionDetail) {
        appNavigation.openBudgetDetailToEntertainmentScreen(Bundle().apply {
            putParcelable("categoryTransaction", categoryTransaction)
        })

    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    override fun initData(savedInstanceState: Bundle?) {
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
        super.initData(savedInstanceState)
        budget = arguments?.getParcelable("budget")
        budget?.let {
            binding.nameLabel.text = it.name
            binding.spentLabel.text = "${currencySymbol}${it.spent}"
            binding.progressBar.max = it.amount.toInt()
            binding.remainTitleLabel.text = if (it.spent > it.amount) requireContext().getString(R.string.overspent) else requireContext().getString(R.string.left)
            binding.progressBar.progress = if (it.spent > it.amount) it.amount.toInt() else it.spent.toInt()
            categoryViewModel.getCategory()
            binding.remainLabel.text = "${currencySymbol}${it.amount - it.spent}"
            getVM().getBudgets(it.accountId)
            binding.budgetLabel.text = "${currencySymbol}${it.amount}"
            var start_date = it.startDate.toFormattedDateString()
            var end_date = it.endDate.toFormattedDateString()
            binding.periodLabel.text = start_date + " - " + end_date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendarToday = Calendar.getInstance()
            val todayDate = LocalDate.parse(dateFormat.format(calendarToday.time), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val endDate = LocalDate.parse(end_date, formatter)
            val daysBetween = ChronoUnit.DAYS.between(todayDate, endDate)
            binding.timeLabel.text = "${daysBetween} " + requireContext().getString(R.string.left_days)
        }
    }

    private fun showData(category: List<CategoryWithTransfer>) {
        val listCategoryTransactionDetail = getVM().toCategoryTransactionDetail(category)
        categoryTransactionAdapter.setDate(listCategoryTransactionDetail)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
