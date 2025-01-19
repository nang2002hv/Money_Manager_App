package com.example.money_manager_app.fragment.entertainment.view

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.TransactionAdapter
import com.example.money_manager_app.base.fragment.BaseFragmentNotRequireViewModel
import com.example.money_manager_app.data.model.CategoryTransactionDetail
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.toWallet
import com.example.money_manager_app.databinding.FragmentEntertainmentBinding
import com.example.money_manager_app.utils.groupTransactionsByDate
import com.example.money_manager_app.viewmodel.MainViewModel

class EntertainmentFragment : BaseFragmentNotRequireViewModel<FragmentEntertainmentBinding>(R.layout.fragment_entertainment) {

    private lateinit var  transactionAdapter : TransactionAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        back()
    }

    fun back(){
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    private fun setAdapter(){
        val currencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)
        transactionAdapter = TransactionAdapter(
            requireContext(),
            currencySymbol,
            mainViewModel.currentAccount.value!!.walletItems.map { it.toWallet() } ,
            mainViewModel.categories.value
        ) {
            val bundle = bundleOf("transaction" to it)
            findNavController().navigate(R.id.action_entertainmentFragment_to_recordFragment, bundle)
        }
        binding.rvEntertainment.adapter = transactionAdapter
        binding.rvEntertainment.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        setAdapter()
        val categoryTransaction = arguments?.getParcelable<CategoryTransactionDetail>("categoryTransaction")
        if(categoryTransaction != null){
            val currencySymbol =
                getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)
            binding.tvTotalExpenseValue.text =  currencySymbol + categoryTransaction.totalAmount.toString()
            val transactions = categoryTransaction?.listTransfer as List<Transaction>
            transactionAdapter.submitList(transactions.groupTransactionsByDate())
        }
    }
}