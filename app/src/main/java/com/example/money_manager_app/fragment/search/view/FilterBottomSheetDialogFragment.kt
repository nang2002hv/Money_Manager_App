package com.example.money_manager_app.fragment.search.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseBottomSheet
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.databinding.FilterBottomSheetBinding
import com.example.money_manager_app.fragment.add.view.expense.ExpenseViewModel
import com.example.money_manager_app.fragment.add.view.income.IncomeViewModel
import com.example.money_manager_app.fragment.search.adapter.IconCategoryAdapter
import com.example.money_manager_app.fragment.search.adapter.SearchInterface
import com.example.money_manager_app.fragment.search.viewmodel.SearchViewModel
import com.example.money_manager_app.fragment.wallet.WalletViewModel
import com.example.money_manager_app.viewmodel.MainViewModel
import java.util.Calendar

class FilterBottomSheetDialogFragment : BaseBottomSheet<FilterBottomSheetBinding>() {

    private val walletViewModel : WalletViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val incomeViewModel : IncomeViewModel by activityViewModels()
    private val expenseViewModel : ExpenseViewModel by activityViewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    private var searchInterface: SearchInterface? = null

    override fun getLayoutId(): Int {
        return R.layout.filter_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletViewModel.getWallets(1)
        binding.startDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.startDate.setText(selectedDate)
            }
        }

        binding.endDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.endDate.setText(selectedDate)
            }
        }
        selectWallet()
        selectCategory()
        applyFilter()
        cancel()
    }


    private fun cancel(){
        binding.cancelFilterButton.setOnClickListener {
            dismiss()
        }
    }

    private fun selectWallet() {
        binding.filterWallet.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val listWallet = walletViewModel.wallets.value.map { it.wallet.name }
                .toTypedArray()

            if (listWallet.isEmpty()) {
                Toast.makeText(requireContext(), "Không có ví nào để chọn!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            builder.setTitle("Chọn ví")
            builder.setItems(listWallet) { _, which ->
                val selectedWallet = listWallet[which]
                for(wallet in walletViewModel.wallets.value){
                    if(wallet.wallet.name == selectedWallet){
                        searchViewModel.setWalletSearch(wallet.wallet)
                    }
                }
                binding.filterWallet.setText(selectedWallet.toString())
            }
            builder.show()
        }
    }


    fun selectCategory(){
        binding.filterCategory.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_category, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
            val listCategoryIncome = incomeViewModel.getCategoryListIncome()
            val listCategoryExpense = expenseViewModel.getCategoryListExpense()
            val listCategory = mutableListOf<CategoryData.Category>()
            listCategory.addAll(listCategoryIncome.filter { it.name != "Others"})
            listCategory.addAll(listCategoryExpense.filter { it.name != "Other"})
            listCategory.sortedBy { it.name }
            listCategory.add(
                CategoryData.Category(
                0, "Other", "expense_14",
                "expense",
                true
            ))
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Chọn danh mục")
                .setView(dialogView)
                .create()
            val adapter = IconCategoryAdapter(listCategory) {
                onItemSelected(it)
                alertDialog.dismiss()
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            alertDialog.show()
        }
    }

    fun onItemSelected(selectedCategory: CategoryData.Category) {
        searchViewModel.setCategorySearch(selectedCategory)
        var listCategory  = mainViewModel.categories.value
        for(category in listCategory){
            if(category.name == selectedCategory.name){
                searchViewModel.setIdCategory(category.id)
            }
        }
        binding.filterCategory.setText(selectedCategory.name)
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    fun setSearchInterface(searchInterface: SearchInterface) {
        this.searchInterface = searchInterface?: return
    }



    private fun applyFilter() {
        binding.applyFilterButton.setOnClickListener {
            searchViewModel.setMinAmount(binding.minAmount.text.toString().toDoubleOrNull() ?: 0.0)
            searchViewModel.setMaxAmount(binding.maxAmount.text.toString().toDoubleOrNull() ?: 0.0)
            searchViewModel.setStartDate(binding.startDate.text.toString())
            searchViewModel.setEndDate(binding.endDate.text.toString())
            searchInterface?.search()
            dismiss()
        }
    }
}
