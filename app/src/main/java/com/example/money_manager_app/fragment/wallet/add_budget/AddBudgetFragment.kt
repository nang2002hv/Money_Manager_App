package com.example.money_manager_app.fragment.wallet.add_budget

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.ColorSpinnerAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.data.model.entity.enums.PeriodType
import com.example.money_manager_app.databinding.FragmentAddBudgetBinding
import com.example.money_manager_app.fragment.selecticon.viewmodel.CategoryViewModel
import com.example.money_manager_app.utils.ColorUtils
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.takeDayOfWeek
import com.example.money_manager_app.utils.takeDayofMonth
import com.example.money_manager_app.utils.takeMonthYear
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toFormattedDateString
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



@AndroidEntryPoint
class AddBudgetFragment : BaseFragment<FragmentAddBudgetBinding, AddBudgetViewModel>(R.layout.fragment_add_budget) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private var budgetEdit: Budget? = null

    override fun getVM(): AddBudgetViewModel {
        val vm: AddBudgetViewModel by viewModels()
        return vm
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            budgetEdit = it.getParcelable("budget")
            val category = it.getString("category")
            if(budgetEdit != null){
                binding.editTextName.setText(budgetEdit?.name)
                binding.editTextAmount.setText(budgetEdit?.amount.toString())
                binding.colorSpinner.setSelection(ColorUtils.getColors(requireContext()).indexOf(budgetEdit?.colorId))
                binding.periodSpinner.setSelection(PeriodType.entries.indexOf(budgetEdit?.periodType))
                binding.selectCategory.text = category
            }
        }
    }



    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        selectCategory()
        obseverData()
        selectColor()
        selectPeriod()
        option()
        back()
        save()
    }


    private fun option() {
        setSpinnerDayOfWeek(R.array.budget_day_of_week)
        setSpinnerDayOfMonth(R.array.budget_day)
        setSpinnerDayOfYear(R.array.budget_month_of_year)
        binding.periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        binding.dayOfMonthLabel.text = requireContext().getString(R.string.day_of_week)
                        binding.dayOfWeekSpinner.visibility = View.VISIBLE
                        binding.dayOfMonthSpinner.visibility = View.GONE
                        binding.dayOfYearLabel.visibility = View.GONE
                        binding.dayOfYearSpinner.visibility = View.GONE
                    }
                    1 -> {
                        binding.dayOfMonthLabel.text = requireContext().getString(R.string.day_of_month)
                        binding.dayOfWeekSpinner.visibility = View.GONE
                        binding.dayOfMonthSpinner.visibility = View.VISIBLE
                        binding.dayOfYearLabel.visibility = View.GONE
                        binding.dayOfYearSpinner.visibility = View.GONE
                    }
                    else -> {
                        binding.dayOfMonthLabel.text = requireContext().getString(R.string.day_of_month)
                        binding.dayOfWeekSpinner.visibility = View.GONE
                        binding.dayOfMonthSpinner.visibility = View.VISIBLE
                        binding.dayOfYearLabel.visibility = View.VISIBLE
                        binding.dayOfYearSpinner.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        if(budgetEdit != null){
            binding.colorSpinner.setSelection(ColorUtils.getColors(requireContext()).indexOf(budgetEdit?.colorId))
            binding.periodSpinner.setSelection(PeriodType.entries.indexOf(budgetEdit?.periodType))
            val initDate = budgetEdit?.initDate?.toFormattedDateString("dd/MM/yyyy")
            when (PeriodType.entries.indexOf(budgetEdit?.periodType)) {
                0 -> {
                    val dayOfWeek = initDate?.takeDayOfWeek("dd/MM/yyyy")
                    binding.dayOfWeekSpinner.setSelection(dayOfWeek!! - 1)
                    binding.dayOfWeekSpinner.visibility = View.VISIBLE
                    binding.dayOfMonthSpinner.visibility = View.GONE
                    binding.dayOfYearLabel.visibility = View.GONE
                    binding.dayOfYearSpinner.visibility = View.GONE
                }
                1 -> {
                    val dayOfMonth = initDate?.takeDayofMonth("dd/MM/yyyy")
                    binding.dayOfMonthSpinner.setSelection(dayOfMonth!! - 1)
                    binding.dayOfWeekSpinner.visibility = View.GONE
                    binding.dayOfMonthSpinner.visibility = View.VISIBLE
                    binding.dayOfYearLabel.visibility = View.GONE
                    binding.dayOfYearSpinner.visibility = View.GONE
                }
                else -> {
                    val dayOfMonth = initDate?.takeDayofMonth("dd/MM/yyyy")
                    val month = initDate?.takeMonthYear("dd/MM/yyyy")
                    binding.dayOfMonthSpinner.setSelection(dayOfMonth!! - 1)
                    binding.dayOfYearSpinner.setSelection(month!! - 1)
                    binding.dayOfWeekSpinner.visibility = View.GONE
                    binding.dayOfMonthSpinner.visibility = View.VISIBLE
                    binding.dayOfYearLabel.visibility = View.VISIBLE
                    binding.dayOfYearSpinner.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setSpinnerDayOfWeek(array: Int){
        val selectPeriod = resources.getStringArray(array)

        val periodTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_period,
            selectPeriod
        )
        binding.dayOfWeekSpinner.adapter = periodTypeAdapter
        binding.dayOfWeekSpinner.setSelection(0)
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }


    private fun setSpinnerDayOfMonth(array: Int){
        val selectPeriod = resources.getStringArray(array)

        val periodTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_period,
            selectPeriod
        )
        binding.dayOfMonthSpinner.adapter = periodTypeAdapter
        binding.dayOfMonthSpinner.setSelection(0)
    }

    private fun setSpinnerDayOfYear(array: Int){
        val selectPeriod = resources.getStringArray(array)

        val periodTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_period,
            selectPeriod
        )
        binding.dayOfYearSpinner.adapter = periodTypeAdapter
        binding.dayOfYearSpinner.setSelection(0)
    }

    fun back(){
        binding.backArrowButton.setOnSafeClickListener {
            findNavController().popBackStack()
        }
    }

    private fun selectPeriod(){
        val selectPeriod = resources.getStringArray(R.array.budget_period)
        val periodTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_period,
            selectPeriod
        )
        binding.periodSpinner.adapter = periodTypeAdapter
        binding.periodSpinner.setSelection(0)
    }

    private fun selectColor(){
        val colorAdapter = ColorSpinnerAdapter(requireContext(), ColorUtils.getColors(requireContext()))
        binding.colorSpinner.adapter = colorAdapter
        binding.colorSpinner.setSelection(0)
    }

    private fun obseverData(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                categoryViewModel.categories.observe(viewLifecycleOwner){
                    showCategory(it)
                }
            }
        }
    }

    private fun showCategory(category : List<CategoryData.Category>){
        var nameCategory =""
        if(category[0].isCheck == true){
            nameCategory= context?.getString(R.string.all_category) ?:""
        } else {
            for (i in category.indices){
                if(category[i].isCheck == true){
                    nameCategory += category[i].name +", "
                }
            }
            nameCategory.removeSuffix(", ")
        }
        binding.selectCategory.text = (if(nameCategory.isEmpty()) context?.getString(R.string.select_category) else nameCategory)
    }

    private fun selectCategory() {
        binding.selectCategory.setOnSafeClickListener {
            appNavigation.openAddBudgetToSelectCategory()
        }
    }

    private fun checkngay() :Pair<Long,Long>{
        val periodType = PeriodType.values()[binding.periodSpinner.selectedItemPosition]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendarToday = Calendar.getInstance()
        var todayDate = dateFormat.format(calendarToday.time).toDateTimestamp()
        var endDate = 0L
        when(periodType){
            PeriodType.WEEKLY -> {
                val listDayOfWeek = resources.getStringArray(R.array.budget_day_of_week)
                val dayOfWeek = binding.dayOfWeekSpinner.selectedItemPosition
                val dayselect = getVM().getDateFromDayOfWeek(listDayOfWeek[dayOfWeek])
                if(todayDate < dayselect.toDateTimestamp()){
                    endDate = dayselect.toDateTimestamp()
                    todayDate = endDate - 7*24*60*60*1000
                } else {
                    todayDate = dayselect.toDateTimestamp()
                    endDate = todayDate + 7*24*60*60*1000
                }
            }
            PeriodType.MONTHLY -> {
                val listDayOfMonth = resources.getStringArray(R.array.budget_day)
                val dayOfMonth = binding.dayOfMonthSpinner.selectedItemPosition
                val dayselect = getVM().getDateFromDayOfMonth(listDayOfMonth[dayOfMonth])
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dayselect.toDateTimestamp()
                if(todayDate < dayselect.toDateTimestamp()){
                    endDate = dayselect.toDateTimestamp()
                    calendar.add(Calendar.MONTH, -1)
                    todayDate = calendar.timeInMillis

                } else {
                    todayDate = dayselect.toDateTimestamp()
                    calendar.add(Calendar.MONTH, 1)
                    endDate = calendar.timeInMillis
                }
            }
            PeriodType.YEARLY -> {
                val listDayOfMonth = resources.getStringArray(R.array.budget_day)
                val dayOfMonth = binding.dayOfMonthSpinner.selectedItemPosition
                val listDayOfYear = resources.getStringArray(R.array.budget_month_of_year)
                val dayOfYear = binding.dayOfYearSpinner.selectedItemPosition
                val dayselect = getVM().getDateFromDayOfMonth(listDayOfMonth[dayOfMonth],listDayOfYear[dayOfYear])
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dayselect.toDateTimestamp()

                if(todayDate < dayselect.toDateTimestamp()){
                    endDate = dayselect.toDateTimestamp()
                    calendar.add(Calendar.YEAR, -1)
                    todayDate = calendar.timeInMillis

                } else {
                    todayDate = dayselect.toDateTimestamp()
                    calendar.add(Calendar.YEAR, 1)
                    endDate = calendar.timeInMillis
                }
            }
        }
        return Pair(todayDate,endDate)
    }

    private fun save() {
        binding.saveButton.setOnSafeClickListener {
            val dateCurrent = System.currentTimeMillis()
            val date = checkngay()
            val amountText = binding.editTextAmount.text.toString()
            val amount = if (amountText.isNotEmpty()) amountText.toDouble() else 0.0
            if (amount > 0) {
                val budget = Budget(
                    name = binding.editTextName.text.toString(),
                    amount = amount,
                    spent = 0,
                    accountId = mainViewModel.accounts.value.first().account.id,
                    colorId = ColorUtils.getColors(requireContext())[binding.colorSpinner.selectedItemPosition],
                    periodType = PeriodType.entries[binding.periodSpinner.selectedItemPosition],
                    initDate = dateCurrent,
                    startDate = date.first,
                    endDate = date.second
                )
                if (budgetEdit != null) {
                    budget.id = budgetEdit?.id ?: 0
                    budget.spent = budgetEdit?.spent ?: 0
                    getVM().editBudget(budget,
                        mainViewModel.categories.value,
                        categoryViewModel.categories.value ?: emptyList())
                    findNavController().popBackStack()
                } else {
                    getVM().insertBudget(
                        budget,
                        mainViewModel.categories.value ?: emptyList(),
                        categoryViewModel.categories.value ?: emptyList()
                    )
                    findNavController().popBackStack()
                }

            }
        }
    }
}