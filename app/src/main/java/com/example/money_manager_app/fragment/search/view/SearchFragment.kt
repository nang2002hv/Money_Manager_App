package com.example.money_manager_app.fragment.search.view

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.fragment.search.viewmodel.SearchViewModel
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.databinding.FragmentSearchBinding
import com.example.money_manager_app.fragment.search.adapter.SearchInterface
import com.example.money_manager_app.fragment.search.adapter.SearchTransactionAdapter
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(R.layout.fragment_search),
    SearchInterface {

    private val searchViewModel : SearchViewModel by activityViewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var  adapter : SearchTransactionAdapter

    override fun getVM(): SearchViewModel {
        val viewModel: SearchViewModel by viewModels()
        return viewModel
    }

    fun onclick(transaction: Transaction) {
        if (transaction is Transfer || transaction is DebtTransaction) {
            val bundle = bundleOf(
                "transaction" to transaction
            )
            findNavController().navigate(R.id.recordFragment, bundle)
        }
        if(transaction is Debt){
            val bundle = bundleOf(
                "debt" to transaction
            )
            findNavController().navigate(R.id.debtDetailFragment, bundle)
        }
    }


    override fun setOnClick() {
        super.setOnClick()
        binding.filterButton.setOnSafeClickListener {
            showFilterBottomSheet()
            searchViewModel.clearFilter()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        back()
        setAdapter()
        observeData()
    }

    private fun setAdapter() {
        mainViewModel.getAccount()
        val currencySymbol = getString(mainViewModel.accounts.value.first().account.currency.symbolRes)
        adapter = SearchTransactionAdapter(requireContext(),this::onclick, listOf(), listOf(), currencySymbol)
        binding.searchResults.layoutManager = LinearLayoutManager(context)
        binding.searchResults.adapter = adapter

    }

    private fun observeData() {
        lifecycleScope.launch {
            searchViewModel.listTransaction.collect { list ->
                adapter.setTransfers(list)
            }
        }

        lifecycleScope.launch {
            mainViewModel.accounts.collect { wallet ->
               adapter.setWallets(wallet.first().walletItems.map { it.wallet })
            }
        }
    }

    fun back() {
        binding.backButton.setOnSafeClickListener {
            searchViewModel.clearFilter()
            adapter.setTransfers(listOf())
            findNavController().popBackStack()
        }
    }

    private fun showFilterBottomSheet() {
        val filterBottomSheet = FilterBottomSheetDialogFragment()
        filterBottomSheet.setSearchInterface(this)
        filterBottomSheet.show(parentFragmentManager, "FilterBottomSheetDialogFragment")
    }

    override fun search() {
        searchViewModel.searchByDateAndAmountAndDesAndCategoryAndWallet(mainViewModel.accounts.value.first().account.id)
    }

}
