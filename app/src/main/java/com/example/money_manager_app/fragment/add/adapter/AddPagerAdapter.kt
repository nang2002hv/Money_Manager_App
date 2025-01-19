package com.example.moneymanager.ui.add.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.money_manager_app.fragment.add.view.expense.AddExpenseFragment
import com.example.money_manager_app.fragment.add.view.income.AddIncomeFragment
import com.example.money_manager_app.fragment.add.view.transfer.AddTranferFragment

class AddPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int =  component.NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 ->  AddIncomeFragment()
            1 -> AddExpenseFragment()
            2 -> AddTranferFragment()
            else -> AddExpenseFragment()
        }
    }


    object component {
        const val NUM_TABS = 3
    }

}