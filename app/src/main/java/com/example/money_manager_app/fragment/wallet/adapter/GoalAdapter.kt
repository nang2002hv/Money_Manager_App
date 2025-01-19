package com.example.money_manager_app.fragment.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.Goal
import com.example.money_manager_app.data.model.entity.GoalDetail
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.databinding.AddNewItemBinding
import com.example.money_manager_app.databinding.GoalItemBinding
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toFormattedDateString

class GoalAdapter(
    private val context: Context,
    private val onItemClick: (Goal) -> Unit,
    private val onAddNewClick: () -> Unit
) : ListAdapter<GoalDetail, RecyclerView.ViewHolder>(GoalDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) TYPE_ADD_NEW else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ITEM) {
            GoalViewHolder(GoalItemBinding.inflate(inflater, parent, false))
        } else {
            AddGoalViewHolder(AddNewItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            val goal = getItem(position)
            (holder as GoalViewHolder).bind(goal)
        } else if (holder is AddGoalViewHolder) {
            // No binding required for the "Add Goal" button
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    inner class AddGoalViewHolder(val binding: AddNewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.addName.text = context.getString(R.string.add_goal)
            binding.root.setOnClickListener { onAddNewClick() }
        }
    }

    inner class GoalViewHolder(val binding: GoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: GoalDetail) {
            binding.nameLabel.text = goal.goal.name
            binding.dateLabel.text = goal.goal.targetDate.toFormattedDateString()
            binding.progressBar.max = 100
            val currentAccumulation =
                goal.transactions.filter { it.type == GoalInputType.DEPOSIT }.sumOf { it.amount } -
                        goal.transactions.filter { it.type == GoalInputType.WITHDRAW }
                            .sumOf { it.amount }
            val progress = currentAccumulation / goal.goal.amount * 100
            binding.progressBar.progress = if (progress >= 100) progress.toFloat() else 100f
            binding.progressBar.text = context.getString(R.string.formatted_double_percentage, progress)
            binding.root.setOnSafeClickListener { onItemClick(goal.goal) }
        }
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_ADD_NEW = 1
    }
}

class GoalDiffCallback : DiffUtil.ItemCallback<GoalDetail>() {
    override fun areItemsTheSame(oldItem: GoalDetail, newItem: GoalDetail): Boolean {
        return oldItem.goal.id == newItem.goal.id
    }

    override fun areContentsTheSame(oldItem: GoalDetail, newItem: GoalDetail): Boolean {
        return oldItem == newItem
    }
}