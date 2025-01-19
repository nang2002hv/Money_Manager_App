package com.example.money_manager_app.navigation

import android.os.Bundle
import android.util.Log
import com.example.money_manager_app.R
import com.example.money_manager_app.base.navigation.BaseNavigatorImpl
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigationImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation {

    override fun openSplashToLanguageScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashScreenFragment_to_languageFragment, bundle)
    }

    override fun openSplashToPasswordScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashScreenFragment_to_passwordFragment, bundle)
    }

    override fun openLanguageToPasswordScreen(bundle: Bundle?) {
        openScreen(R.id.action_languageFragment_to_passwordFragment, bundle)
    }

    override fun openPasswordToCreateAccountScreen(bundle: Bundle?) {
        openScreen(R.id.action_passwordFragment_to_createAccountFragment, bundle)
    }

    override fun openMainScreenToAddDebtScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addDebtFragment, bundle)
    }

    override fun openMainScreenToDebtDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_debtDetailFragment, bundle)
    }

    override fun openDebtDetailToAddDebtTransactionScreen(bundle: Bundle?) {
        openScreen(R.id.action_debtDetailFragment_to_addDebtTransactionFragment, bundle)
    }

    override fun openMainScreenToGoalDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_goalDetailFragment, bundle)
    }

    override fun openGoalDetailToAddGoalTransactionScreen(bundle: Bundle?) {
        openScreen(R.id.action_goalDetailFragment_to_addGoalTransactionFragment, bundle)
    }

    override fun openMainScreenToAddGoalScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addGoalFragment, bundle)
    }

    override fun openMainScreenToCreateAccountScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_createAccountFragment, bundle)
    }

    override fun openGoalDetailToAddGoalScreen(bundle: Bundle?) {
        openScreen(R.id.action_goalDetailFragment_to_addGoalFragment, bundle)
    }

    override fun openDebtDetailToAddDebtScreen(bundle: Bundle?) {
        openScreen(R.id.action_debtDetailFragment_to_addDebtFragment, bundle)
    }

    override fun openCreateAccountToMainScreen(bundle: Bundle?) {
        openScreen(R.id.action_createAccountFragment_to_mainFragment, bundle)
    }

    override fun openPasswordToMainScreen(bundle: Bundle?) {
        openScreen(R.id.action_passwordFragment_to_mainFragment, bundle)
    }

    override fun openMainScreenToAddWalletScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addWalletFragment, bundle)
    }

    override fun openMainScreenToWalletDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_walletDetailFragment, bundle)
    }

    override fun openWalletDetailToAddWalletScreen(bundle: Bundle?) {
        openScreen(R.id.action_walletDetailFragment_to_addWalletFragment, bundle)
    }

    override fun openMainScreenToAddFragmentScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addFragment, bundle)
    }

    override fun openMainScreenToAddBudgetScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addBudgetFragment, bundle)
    }

    override fun openAddBudgetToSelectCategory(bundle: Bundle?) {
        openScreen(R.id.action_addBudgetFragment_to_MultipleSelectionCatrgoryFragment, bundle)
    }

    override fun openMainScreenToBudgetDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_detailBudgetFragment, bundle)
    }

    override fun openBudgetDetailToAddBudgetScreen(bundle: Bundle?) {
        openScreen(R.id.action_detailBudgetFragment_to_addBudgetFragment, bundle)
    }

    override fun openWalletDetailScreenToStatisticScreen(bundle: Bundle?) {
        openScreen(R.id.action_walletDetailFragment_to_statisticFragment, bundle)
    }

    override fun openBudgetDetailToEntertainmentScreen(bundle: Bundle?) {
        openScreen(R.id.action_detailBudgetFragment_to_entertainmentFragment, bundle)
    }

    override fun openEntertainmentToRecord(bundle: Bundle?) {
        openScreen(R.id.action_entertainmentFragment_to_recordFragment, bundle)
    }

    override fun openAddBudgetToWalletScreen(bundle: Bundle?) {
        openScreen(R.id.action_addBudgetFragment_to_walletFragment, bundle)
    }

    override fun openWalletDetailToToAddFragmentScreen(bundle: Bundle?) {
        openScreen(R.id.action_walletDetailFragment_to_addFragment, bundle)
    }


    override fun openWalletDetailScreenToAddFragmentScreen(bundle: Bundle?) {
        openScreen(R.id.action_walletDetailFragment_to_addFragment, bundle)
    }

    override fun openStatisticScreenToStructureScreen(bundle: Bundle?) {
        openScreen(R.id.action_statisticFragment_to_structureFragment, bundle)
    }

    override fun openStatisticScreenToTransactionScreen(bundle: Bundle?) {
        openScreen(R.id.action_statisticFragment_to_transactionFragment, bundle)
    }

    override fun openStatisticScreenToCreateAccountScreen(bundle: Bundle?) {
        openScreen(R.id.action_statisticFragment_to_createAccountFragment)
    }

    override fun openDebtDetailScreenToRecordScreen(bundle: Bundle?) {
        openScreen(R.id.action_debtDetailFragment_to_recordFragment, bundle)
    }

    override fun openMainScreenToAddFragment(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_addFragment, bundle)
    }

    override fun openMainScreenToLanguageScreen(bundle: Bundle?) {
        openScreen(R.id.action_mainFragment_to_languageFragment, bundle)
    }

    companion object {
        private const val TAG = "AppNavigationImpl"
    }
}
