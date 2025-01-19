package com.example.money_manager_app.fragment.create_account.view

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.money_manager_app.R
import com.example.money_manager_app.fragment.create_account.viewmodel.CreateAccountViewModel
import com.example.money_manager_app.base.fragment.BaseFragmentNotRequireViewModel
import com.example.money_manager_app.databinding.FragmentCreateAccountBinding
import com.example.money_manager_app.fragment.create_account.adapter.CreateAccountPagerAdapter
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment :
    BaseFragmentNotRequireViewModel<FragmentCreateAccountBinding>(R.layout.fragment_create_account) {

    private lateinit var adapter: CreateAccountPagerAdapter
    private val createAccountViewModel: CreateAccountViewModel by activityViewModels()
    private var isAddAccount = false

    private val fragments = listOf(
        CreateAccountDetailFragment.newInstance(ARG_ADD_ACCOUNT),
        CreateAccountDetailFragment.newInstance(ARG_SELECT_CURRENCY),
        CreateAccountDetailFragment.newInstance(ARG_INIT_AMOUNT)
    )

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        arguments?.let {
            isAddAccount = it.getBoolean("isAddAccount", false)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        adapter = CreateAccountPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                createAccountViewModel.setCurrentPage(position)
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        createAccountViewModel.currentPage.observe(viewLifecycleOwner) {
            binding.ivBack.isVisible = isAddAccount || it != 0

            when (it) {
                0 -> binding.viewPager.currentItem = 0
                1 -> binding.viewPager.currentItem = 1
                2 -> binding.viewPager.currentItem = 2
                else -> {
                    appNavigation.openCreateAccountToMainScreen()
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.ivBack.setOnSafeClickListener {
            val isBack = createAccountViewModel.backPage()
            if (isBack) {
                appNavigation.navigateUp()
            }
        }
    }

    companion object {
        const val ARG_ADD_ACCOUNT = 0
        const val ARG_SELECT_CURRENCY = 1
        const val ARG_INIT_AMOUNT = 2
        const val TAG = "CreateAccountFragment"
    }

}