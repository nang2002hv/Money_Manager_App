package com.example.money_manager_app.fragment.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.CalendarRecord
import com.example.money_manager_app.databinding.FragmentCalendarBinding
import com.example.money_manager_app.fragment.calendar.viewmodel.CalendarViewModel
import com.example.money_manager_app.fragment.detail.view.DetailDayFragment
import com.example.money_manager_app.utils.CalendarHelper
import com.example.money_manager_app.viewmodel.MainViewModel
import com.example.moneymanager.fragment.calendar.adapter.CalendarAdapter
import kotlinx.coroutines.launch

import java.util.*

class CalendarFragment : Fragment(), View.OnClickListener, CalendarAdapter.OnItemClickListener {

    private lateinit var binding: FragmentCalendarBinding

    private lateinit var adapter: CalendarAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private var date: Date? = null
    private lateinit var recyclerView: RecyclerView
    private val calendarViewModel : CalendarViewModel by activityViewModels()
    private val mainViewmodel : MainViewModel by activityViewModels()

    private fun setUpLayout() {
        val currentCurrency = mainViewmodel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
        recyclerView = binding.recyclerView
        adapter = CalendarAdapter(currencySymbol,requireContext())
        adapter.setListener(this)
        gridLayoutManager = GridLayoutManager(requireContext(), 7)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        binding.backImage.setOnClickListener(this)
        binding.nextImage.setOnClickListener(this)
        binding.dateLabel.setOnClickListener(this)
    }

    private fun setUpLayoutContent(date: Date?) {
        calendarViewModel.getTransactionMonth(date!!, mainViewmodel.currentAccount.value?.account?.id ?: 0)
        fetchDataAndUpdateUI(date)
    }

    private fun fetchDataAndUpdateUI(date: Date) {
        updateUI(date)
    }

    private fun updateUI(date: Date) {
        binding.dateLabel.text = CalendarHelper.getFormattedMonthlyDate(date)
        adapter.setList(calendarViewModel.calendarRecord.value)
        adapter.setDate(date)
        adapter.notifyDataSetChanged()
    }



    fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                calendarViewModel.detailTransaction.collect {
                    calendarViewModel.setCalendarRecord(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                calendarViewModel.calendarRecord.collect {
                    setUpSummary(it)
                    adapter.setList(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setUpSummary(calendarRecord : List<CalendarRecord>) {
        var income = 0.0
        var expense = 0.0

        for (record in calendarRecord) {
            income += record.income
            expense += record.expense
        }
        val total = income - expense


        val currentCurrency = mainViewmodel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
        binding.incomeLabel.text = context?.getString(
            R.string.positive_money_amount, currencySymbol, income
        )
        binding.expenseLabel.text = context?.getString(
            R.string.negative_money_amount, currencySymbol, expense
        )
        binding.amountLabel.text = if (total >= 0) {
            context?.getString(
                R.string.positive_money_amount, currencySymbol, total
            )
        } else {
            context?.getString(
                R.string.negative_money_amount, currencySymbol, -total
            )
        }
    }

    override fun onItemClick(view: View) {
        view.tag?.let {
            val calendarDay = CalendarHelper.getCalendarDay(date!!, it as Int)
            val bundle = bundleOf("date" to calendarDay)
            val detailDayFragment = DetailDayFragment()
            detailDayFragment.arguments = bundle
            findNavController().navigate(R.id.detailDayFragment, bundle)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backImage -> {
                date = CalendarHelper.incrementMonth(date!!, -1)
                setUpLayoutContent(date)
            }
            R.id.dateLabel -> {
//                val intent = Intent(activity, MonthlyPicker::class.java)
//                intent.putExtra("date", date?.time)
//                startActivityForResult(intent, 6)
            }
            R.id.nextImage -> {
                date = CalendarHelper.incrementMonth(date!!, 1)
                setUpLayoutContent(date)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        date = CalendarHelper.getInitialDate()
        mainViewmodel.getAccount()
        setUpLayout()
        observeData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpLayoutContent(date)
    }

    override fun onStop() {
        super.onStop()
        adapter.setList(listOf())
    }

}

