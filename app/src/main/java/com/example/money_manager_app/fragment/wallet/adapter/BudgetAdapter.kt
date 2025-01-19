package com.example.money_manager_app.fragment.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.Budget
import com.example.money_manager_app.databinding.AddNewItemBinding
import com.example.money_manager_app.databinding.BudgetItemBinding

class BudgetAdapter(
    private val context: Context,
    private var list: List<Budget>,
    private val currentCurrencySymbol: String,
    private val onItemClick: (Budget) -> Unit,
    private val onAddNewClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class BudgetViewHolder(val binding: BudgetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(budget: Budget) {
            binding.dateLabel.text = budget.periodType.toString()
            binding.nameLabel.text = budget.name
            binding.progressBar.max = budget.amount.toInt()
            if (budget.spent > budget.amount) {
                binding.progressBar.progress = budget.amount.toInt()
                binding.amountLabel.text =  context.getString(R.string.overspent) + " ${currentCurrencySymbol} ${budget.spent-budget.amount}"
            } else {
                binding.progressBar.progress =  budget.spent
                binding.amountLabel.text = context.getString(R.string.remain) + " ${currentCurrencySymbol} ${budget.amount-budget.spent}"
            }
            binding.root.setOnClickListener { onItemClick(budget) }
        }
    }

    inner class AddBudgetViewHolder(val binding: AddNewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.addName.text = context.getString(R.string.add_budget)
            binding.root.setOnClickListener { onAddNewClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ITEM) {
            BudgetViewHolder(BudgetItemBinding.inflate(inflater, parent, false))
        } else {
            AddBudgetViewHolder(AddNewItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_ADD_NEW else TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            val budget = list[position]
            (holder as BudgetViewHolder).bind(budget)
        } else if (holder is AddBudgetViewHolder) {
            // No binding required for the "Add Budget" button
        }
    }

    fun setList(newList: List<Budget>) {
        list = newList
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_ADD_NEW = 1
    }
}