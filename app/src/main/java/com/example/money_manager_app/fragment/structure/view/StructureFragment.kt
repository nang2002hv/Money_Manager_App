package com.example.money_manager_app.fragment.structure.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.databinding.FragmentStructureBinding
import com.example.money_manager_app.fragment.statistic.adapter.StaticInterface
import com.example.money_manager_app.fragment.statistic.view.FilterTimeBottomSheetDialogFragment
import com.example.money_manager_app.fragment.structure.adapter.StructApdapter
import com.example.money_manager_app.fragment.structure.viewmodel.StructureViewModel
import com.example.money_manager_app.utils.CalendarHelper
import com.example.money_manager_app.utils.DateHelper
import com.example.money_manager_app.utils.TimeType
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class StructureFragment : BaseFragment<FragmentStructureBinding, StructureViewModel>(R.layout.fragment_structure),View.OnClickListener,
    StaticInterface {

    override fun getVM(): StructureViewModel {
        val viewModel: StructureViewModel by activityViewModels()
        return viewModel
    }

    private var time = TimeType.MONTHLY
    private var date = Date()
    private var check = true
    private var wallets: List<Wallet> = ArrayList()
    private lateinit var structApdapter : StructApdapter
    private val mainViewModel : MainViewModel by activityViewModels()

    override fun initData(savedInstanceState: Bundle?) {
        setUpLayout()
        super.initData(savedInstanceState)
        time = arguments?.getParcelable<TimeType>("type") ?: TimeType.MONTHLY
        val stringDate = arguments?.getString("dateStart")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if(stringDate != null) {
            date = dateFormat.parse(stringDate)
        }
        wallets = arguments?.getParcelableArrayList("wallets") ?: ArrayList()
        if(!wallets.isEmpty()) {
            setBalance()
            observeData()
        }
        binding.tvIncome.setOnClickListener{
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.customer_select_category_income)
            it.background = drawable
            check = false
            binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.tvExpense.background = null
            binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            structApdapter.setPieStatsList(getVM().listStatsIncome.value)
        }

        binding.tvExpense.setOnClickListener{
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.customer_select_category_expense)
            it.background = drawable
            check = true
            binding.tvIncome.background = null
            binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            structApdapter.setPieStatsList(getVM().listStatsExpense.value)
        }
    }

    private fun setBalance() {
        getVM().setWallets(wallets)
    }

    private fun observeData() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().calendarSummary.collect {
                    if(check == true){
                        structApdapter.setPieStatsList(getVM().listStatsExpense.value)
                    } else {
                        structApdapter.setPieStatsList(getVM().listStatsIncome.value)
                    }
                }
            }
        }

        if(check == true){
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    getVM().listStatsExpense.collect {
                        structApdapter.setPieStatsList(it)
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    getVM().listStatsIncome.collect {
                        structApdapter.setPieStatsList(it)
                    }
                }
            }
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        back()
        date = Date()
        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
    }

    fun back() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpLayout() {
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        structApdapter = StructApdapter(requireContext(),currencySymbol)
        binding.recyclerView.adapter = structApdapter
        binding.backImage.setOnClickListener(this)
        binding.nextImage.setOnClickListener(this)
        binding.dateLabel.setOnClickListener(this)
    }

    private fun setUpLayoutContent(date: Date?, idAccount : Long) {
        if(date != null) {
            binding.dateLabel.text = CalendarHelper.getFormattedDailyDate(date)
            when(time) {
                TimeType.MONTHLY -> {
                    binding.dateLabel.text = CalendarHelper.getFormattedDailyDate(date)
                    var dateStart = DateHelper.getDateMonth(date)
                    getVM().getCalendarSummary(wallets,dateStart.first.toDateTimestamp(),dateStart.second.toDateTimestamp(),idAccount)
                }
                TimeType.DAILY -> {
                    binding.dateLabel.text = CalendarHelper.getFormattedDailyDate(date)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateStart = dateFormat.format(date)
                    getVM().getCalendarSummary(wallets,dateStart.toDateTimestamp(),dateStart.toDateTimestamp(), idAccount)
                }
                TimeType.WEEKLY -> {
                    binding.dateLabel.text = CalendarHelper.getFormattedWeeklyDate(requireContext(),date)
                    val dateStart = DateHelper.getDateWeek(date)
                    getVM().getCalendarSummary(wallets,dateStart.first.toDateTimestamp(),dateStart.second.toDateTimestamp(), idAccount)
                }
                TimeType.YEARLY -> {
                    binding.dateLabel.text = CalendarHelper.getFormattedYearlyDate(date)
                    val dateStart = DateHelper.getDateYear(date)
                    getVM().getCalendarSummary(wallets,dateStart.first.toDateTimestamp(),dateStart.second.toDateTimestamp(), idAccount)

                }
                TimeType.ALL -> {
                    binding.dateLabel.text = context?.getString(R.string.all)
                    getVM().getCalendarSummary(wallets, idAccount)

                }
                TimeType.CUSTOM -> {

                }
            }
        }
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    override fun onClick(view: View) {
        when(time) {
            TimeType.MONTHLY -> {
                when (view.id) {
                    R.id.backImage -> {
                        date = CalendarHelper.incrementMonth(date!!, -1)
                        setUpLayoutContent(date,mainViewModel.currentAccount.value!!.account.id)
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
                        date = CalendarHelper.incrementMonth(date!!, +1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                }
            }

            TimeType.DAILY -> {
                when (view.id) {
                    R.id.backImage -> {
                        date = CalendarHelper.incrementDay(date!!, -1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
                        date = CalendarHelper.incrementDay(date!!, +1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                }
            }
            TimeType.WEEKLY -> {
                when (view.id) {
                    R.id.backImage -> {
                        date = CalendarHelper.incrementWeek(date!!, -1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
                        date = CalendarHelper.incrementWeek(date!!, +1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                }
            }
            TimeType.YEARLY -> {
                when (view.id) {
                    R.id.backImage -> {
                        date = CalendarHelper.incrementYear(date!!, -1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
                        date = CalendarHelper.incrementYear(date!!, +1)
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                }
            }
            TimeType.ALL -> {
                when(view.id) {
                    R.id.backImage -> {
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
                        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
                    }
                }
            }
            TimeType.CUSTOM -> {
                when(view.id) {
                    R.id.backImage -> {
//                        setUpLayoutContentCustom()
                    }
                    R.id.dateLabel -> {
                        val filterTimeBottomSheetDialogFragment = FilterTimeBottomSheetDialogFragment()
                        filterTimeBottomSheetDialogFragment.setStaticInterfacce(this)
                        filterTimeBottomSheetDialogFragment.show(parentFragmentManager, filterTimeBottomSheetDialogFragment.tag)
                    }
                    R.id.nextImage -> {
//                        setUpLayoutContentCustom()
                    }
                }
            }
        }
    }

    fun setUpLayoutContentCustom(startDate: Date, endDate: Date) {
        binding.dateLabel.text = CalendarHelper.getFormattedCustomDate(requireContext(),startDate,endDate)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStart = dateFormat.format(startDate)
        val dateEnd = dateFormat.format(endDate)
        getVM().getCalendarSummary(wallets,dateStart.toDateTimestamp(),dateEnd.toDateTimestamp(), mainViewModel.currentAccount.value!!.account.id)
    }

    override fun onClickTime(timeType: TimeType) {
        time = timeType
        date = Date()
        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
    }

    override fun onClickTime(startDate: Date, endDate: Date) {
        time = TimeType.CUSTOM
        setUpLayoutContentCustom(startDate,endDate)
    }

}