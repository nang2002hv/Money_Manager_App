package com.example.money_manager_app.fragment.wallet.goal_detail

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.toWallet
import com.example.money_manager_app.databinding.AlertDialogBinding
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

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    override fun initToolbar() {
        super.initToolbar()

        binding.backButton.setOnSafeClickListener {
            onBack()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mainViewModel.currentAccount.value?.let {
            currentCurrencySymbol = getString(it.account.currency.symbolRes)
        }
        val wallets =
            mainViewModel.currentAccount.value?.walletItems?.map { it.toWallet() } ?: listOf()

        binding.apply {
            transactionAdapter = TransactionAdapter(
                requireContext(),
                currentCurrencySymbol,
                wallets,
                listOf()
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
                            goalLabel.text = getString(
                                R.string.money_amount,
                                currentCurrencySymbol,
                                it.amountGoal
                            )
                            percentLabel.text = getString(R.string.percent, it.progress)
                            goalDateLabel.text = it.goalDate
                            timeLabel.text = getString(R.string.days_left, it.daysLeft)
                            progressBar.progress = it.progress
                            progressBar.indeterminateTintList =
                                ContextCompat.getColorStateList(requireContext(), it.colorId)
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
            val alertDialogBinding = AlertDialogBinding.inflate(layoutInflater)

            val alert = AlertDialog.Builder(requireContext()).create()
            alert.setView(alertDialogBinding.root)
            alert.setCancelable(true)
            alertDialogBinding.deleteImageView.setOnClickListener {
                getVM().deleteGoal()
                alert.dismiss()
                appNavigation.navigateUp()
            }
            alertDialogBinding.cannelImageView.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }
        binding.depositWrapper.setOnSafeClickListener {
            val goalToSend = getVM().goal.value?.goal ?: goal
            appNavigation.openGoalDetailToAddGoalTransactionScreen(Bundle().apply {
                putParcelable(GOAL_ACTION_TYPE,GoalInputType.DEPOSIT)
                putLong("goal", goalToSend?.id ?: 0)
            })
        }
        binding.withdrawWrapper.setOnSafeClickListener {
            val goalToSend = getVM().goal.value?.goal ?: goal
            appNavigation.openGoalDetailToAddGoalTransactionScreen(Bundle().apply {
                putParcelable(GOAL_ACTION_TYPE,GoalInputType.WITHDRAW)
                putLong("goal", goalToSend?.id ?: 0)
            })
        }
    }

    companion object{
        const val GOAL_ACTION_TYPE = "GOAL_ACTION_TYPE"
    }
}