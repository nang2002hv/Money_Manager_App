package com.example.money_manager_app.fragment.transaction.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.TransactionAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.databinding.FragmentTransactionBinding
import com.example.money_manager_app.fragment.statistic.adapter.StaticInterface
import com.example.money_manager_app.fragment.statistic.view.FilterTimeBottomSheetDialogFragment
import com.example.money_manager_app.fragment.structure.viewmodel.StructureViewModel
import com.example.money_manager_app.fragment.transaction.viewmodel.TransactionViewModel
import com.example.money_manager_app.utils.CalendarHelper
import com.example.money_manager_app.utils.DateHelper
import com.example.money_manager_app.utils.TimeType
import com.example.money_manager_app.utils.groupTransactionsByDate
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TransactionFragment : BaseFragment<FragmentTransactionBinding, TransactionViewModel>(R.layout.fragment_transaction),View.OnClickListener,
    StaticInterface {

    override fun getVM(): TransactionViewModel {
        val viewModel: TransactionViewModel by activityViewModels()
        return viewModel
    }

    private var currencySymbol = ""
    private var time = TimeType.MONTHLY
    private var date = Date()
    private var check = true
    private var transactionAdapter: TransactionAdapter? = null
    private var wallets: List<Wallet> = ArrayList()
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
        setAdapter()
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    private fun setAdapter() {
        transactionAdapter = TransactionAdapter(
            requireContext(),
            currencySymbol,
            wallets,
            mainViewModel.categories.value
        ) {
            if (it is Transfer || it is DebtTransaction) {
                val bundle = bundleOf(
                    "transaction" to it
                )
                findNavController().navigate(R.id.recordFragment, bundle)
            }
            if (it is Debt) {
                val bundle = bundleOf(
                    "debt" to it
                )
                findNavController().navigate(R.id.debtDetailFragment, bundle)
            }
        }
    }

    private fun setBalance() {
        getVM().setWallets(wallets)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().wallets.collect{
                    val balance = getVM().getWallets(it)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().calendarSummary.collect {
                    binding.incomeLabel.text =  context?.getString(R.string.negative_money_amount, currencySymbol, it.income)
                    binding.expenseLabel.text = context?.getString(R.string.positive_money_amount, currencySymbol, it.expense)
                    if(it.income > it.expense) {
                        binding.netLabel.text = context?.getString(R.string.negative_money_amount, currencySymbol, it.income - it.expense)
                    } else {
                        binding.netLabel.text = context?.getString(R.string.positive_money_amount, currencySymbol, it.income - it.expense)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().listTransaction.collect {
                    transactionAdapter?.submitList(it.groupTransactionsByDate())
                    binding.recyclerView.adapter = transactionAdapter
                }
            }
        }

    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        back()
        date = Date()
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        currencySymbol = getString(currentCurrency.symbolRes)
        setUpLayoutContent(date, mainViewModel.currentAccount.value!!.account.id)
    }

    fun back() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpLayout() {
        val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
        val currencySymbol = getString(currentCurrency.symbolRes)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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