package com.example.money_manager_app.fragment.wallet.goal_detail

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.adapter.TransactionAdapter
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.databinding.FragmentGoalDetailBinding
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoalDetailFragment :
    BaseFragment<FragmentGoalDetailBinding, GoalDetailViewModel>(R.layout.fragment_goal_detail) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var goal: Goal? = null
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var currentCurrencySymbol: String
    override fun getVM(): GoalDetailViewModel {
        val vm: GoalDetailViewModel by viewModels()
        return vm
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            goal = it.getParcelable("goal")
            getVM().getGoalDetail(goal!!.id)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        currentCurrencySymbol =
            getString(mainViewModel.currentAccount.value!!.account.currency.symbolRes)

        binding.apply {
            transactionAdapter = TransactionAdapter(
                requireContext(),
                currentCurrencySymbol,
                mainViewModel.currentAccount.value!!.wallets
            )
            goalTransactionRv.adapter = transactionAdapter
            goalTransactionRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().goalDetailItem.collect {
                    it?.let {
                        binding.apply {
                            nameLabel.text = it.title
                            saveLabel.text = getString(
                                R.string.money_amount,
                                currentCurrencySymbol,
                                it.saveAmount
                            )
                            remainLabel.text = getString(
                                R.string.money_amount,
                                currentCurrencySymbol,
                                it.remainAmount
                            )
                            goalDateLabel.text = it.goalDate
                            timeLabel.text = it.daysLeft
                            progressBar.progress = it.progress
                            transactionAdapter.submitList(it.transactions)
                        }
                    }
                }
            }
        }

    }

    override fun setOnClick() {
        super.setOnClick()
        binding.editButton.setOnSafeClickListener {
            val goalToSend = getVM().goal.value?.goal ?: goal
            appNavigation.openGoalDetailToAddGoalScreen(Bundle().apply {
                putParcelable("goal", goalToSend)
            })
        }
        binding.deleteButton.setOnSafeClickListener {
            getVM().deleteGoal()
            appNavigation.navigateUp()
        }
    }
}