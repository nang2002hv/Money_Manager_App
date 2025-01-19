package com.example.money_manager_app.fragment.main

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.AccountWithWalletItem
import com.example.money_manager_app.data.model.entity.AccountWithWallet
import com.example.money_manager_app.databinding.FragmentMainScreenBinding
import com.example.money_manager_app.datasource.LanguageDataSource
import com.example.money_manager_app.fragment.main.adapter.MainPagerAdapter
import com.example.money_manager_app.fragment.main.fragment.AccountSelectorBottomSheet
import com.example.money_manager_app.utils.CategoryUtils
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainScreenFragment :
    BaseFragment<FragmentMainScreenBinding, MainScreenViewModel>(R.layout.fragment_main_screen) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var vpAdapter: MainPagerAdapter
    override fun getVM(): MainScreenViewModel {
        val vm: MainScreenViewModel by viewModels()
        return vm
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        vpAdapter = MainPagerAdapter(this)
        binding.mainViewPager.adapter = vpAdapter

        binding.bottomNavigation.background = null
        binding.bottomNavigation.menu.findItem(R.id.more_selector).isEnabled = false

        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> binding.mainViewPager.setCurrentItem(0, false)
                R.id.action_calendar -> binding.mainViewPager.setCurrentItem(1, false)
                R.id.action_statistic -> binding.mainViewPager.setCurrentItem(2, false)
                R.id.action_wallet -> binding.mainViewPager.setCurrentItem(3, false)
            }
            true
        }

        binding.mainViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                getVM().setCurrentFragmentId(position)
                when (position) {
                    0 -> binding.bottomNavigation.selectedItemId = R.id.action_home
                    1 -> binding.bottomNavigation.selectedItemId = R.id.action_calendar
                    2 -> binding.bottomNavigation.selectedItemId = R.id.action_statistic
                    3 -> binding.bottomNavigation.selectedItemId = R.id.action_wallet
                }
            }
        })
        binding.addFab.setOnClickListener {
            when (getVM().currentFragmentId.value) {
                0 -> {
                    // Action for Home Fragment
                    appNavigation.openMainScreenToAddFragment()
                }

                1 -> {
                    // Action for Calendar Fragment
//                    findNavController().navigate(R.id.calendarFragment)
                }

                2 -> {
                    // Action for Statistic Fragment
//                    findNavController().navigate(R.id.addStatisticFragment)
                }

                3 -> {
                    // Action for Wallet Fragment
//                    findNavController().navigate(R.id.addWalletFragment)
                }
            }
        }
        checkOnFirstRun()
    }

    override fun initToolbar() {
        super.initToolbar()

        binding.profileName.setOnSafeClickListener {
            mainViewModel.accounts.value.let { accounts ->
                mainViewModel.currentAccount.value?.let { currentAccount ->
                    openAccountSelector(accounts, currentAccount)
                }
            }
        }

        binding.dropdownIcon.setOnSafeClickListener {
            mainViewModel.accounts.value.let { accounts ->
                mainViewModel.currentAccount.value?.let { currentAccount ->
                    openAccountSelector(accounts, currentAccount)
                }
            }
        }

        binding.languageIcon.setOnSafeClickListener {
            appNavigation.openMainScreenToLanguageScreen(Bundle().apply {
                putBoolean(IS_LANGUAGE_SETTING, true)
            })
        }
    }

    private fun checkOnFirstRun() {
        val sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("firstRun", true)
        if (isFirstRun) {
            val listCategory = CategoryUtils.listCategory
            mainViewModel.insertCategory(listCategory)
            sharedPreferences.edit().putBoolean("firstRun", false).apply()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentAccount.collect {
                    it?.let {
                        binding.profileName.text = it.account.nameAccount
                    }
                }
            }
        }
        lifecycleScope.launch {
            mainViewModel.currentLanguage.observe(viewLifecycleOwner, Observer {
                val languages = LanguageDataSource.getLanguageList()
                val imgSrc = languages.find { language -> language.locale == it }?.flag
                binding.languageIcon.setImageResource(imgSrc ?: R.drawable.ic_english)
            })
        }
    }

    private fun openAccountSelector(
        accounts: List<AccountWithWalletItem>, currentAccount: AccountWithWalletItem
    ) {
        val accountSelector = AccountSelectorBottomSheet(
            accounts,
            currentAccount,
            { account -> selectAccount(account) },
            ::addAccount,
            mainViewModel.hiddenBalance.value ?: false
        )

        accountSelector.show(parentFragmentManager, "AccountSelectorBottomSheet")
    }

    private fun selectAccount(account: AccountWithWalletItem) {
        mainViewModel.setCurrentAccount(account)

    }

    private fun addAccount() {
        appNavigation.openMainScreenToCreateAccountScreen(Bundle().apply {
            putBoolean("isAddAccount", true)
        })
    }

    companion object {
        private const val TAG = "MainScreenFragment"
        public const val IS_LANGUAGE_SETTING = "IS_LANGUAGE_SETTING"
    }
}
