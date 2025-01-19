package com.example.money_manager_app.fragment.detail.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.toWallet
import com.example.money_manager_app.databinding.FragmentDetailDayBinding
import com.example.money_manager_app.fragment.detail.adapter.DetailDayAdapter
import com.example.money_manager_app.fragment.detail.viewmodel.DetailViewModel
import com.example.money_manager_app.fragment.wallet.WalletViewModel
import com.example.money_manager_app.utils.CurrencyTypeConverter
import com.example.money_manager_app.utils.totalMoneyDay
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class DetailDayFragment : Fragment() {

    private var _binding: FragmentDetailDayBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel : DetailViewModel by activityViewModels()
    private val walletViewModel : WalletViewModel by activityViewModels()
    private var dateTransfer : Long = 0
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: DetailDayAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        ShowDay(bundle!!)
        setAdapter()
        observeData()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            detailViewModel.detailTransaction.collect { transaction ->
                adapter.setTransfers(transaction)
                binding.tvTotalMoneyDay.text = transaction.totalMoneyDay(transaction).toString()
            }
        }
    }


    fun setAdapter() {
        var currencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)
        val wallets = mainViewModel.currentAccount.value!!.walletItems?.map { it.toWallet() }
        adapter = DetailDayAdapter(listOf(),currencySymbol,requireContext(), wallets, ::onClick)
        binding.rvDetailDay.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDetailDay.adapter = adapter
    }

    fun onClick(transaction: Transaction) {
        if (transaction is Transfer || transaction is DebtTransaction) {
            val bundle = bundleOf(
                "transaction" to transaction
            )
            findNavController().navigate(R.id.recordFragment, bundle)
        }
        if (transaction is Debt) {
            val bundle = bundleOf(
                "debt" to transaction
            )
            findNavController().navigate(R.id.debtDetailFragment, bundle)
        }
    }

    fun ShowDay(bundle: Bundle) {
        val date: Date? = bundle.getSerializable("date") as? Date
        date?.let {
            val calendar = Calendar.getInstance().apply { time = it }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            binding.tvDate.text = day.toString()
            calendar.set(year!!.toInt(), month!!.toInt() - 1, day!!.toInt())
            binding.tvDayOfWeek.text = CurrencyTypeConverter().getDayOfWeekString(dayOfWeek)
            binding.tvMonthYear.text = "th $month $year"
            dateTransfer = CurrencyTypeConverter().transfersDay(day.toString(), month.toString(), year.toString())
            Log.i("DetailDayFragment", dateTransfer.toString())
            val idAccount = mainViewModel.currentAccount.value?.account?.id
            walletViewModel.getWallets(idAccount!!)
            detailViewModel.getTransaction(dateTransfer, idAccount!!)
        }
    }

    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}