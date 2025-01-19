package com.example.money_manager_app.fragment.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.example.money_manager_app.data.model.CategoryTransactionDetail
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.databinding.ItemCategoryTransactionBinding
import com.example.money_manager_app.databinding.ItemHeaderBinding

class CategoryTransactionAdapter(
    private var categoryTransactionList: List<CategoryTransactionDetail>,
    private val currentCurrencySymbol: String,
    private val onTransactionItemClick: (CategoryTransactionDetail) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemCategoryTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryTransaction: CategoryTransactionDetail) {
            binding.ivCategory.setImageResource(categoryTransaction.iconId)
            binding.nameCategory.text = categoryTransaction.name
            binding.countCategory.text = categoryTransaction.totalCategoryTransaction.toString()
            binding.amountCategory.text = "-${currentCurrencySymbol}  ${categoryTransaction.totalAmount}"
            binding.root.setOnClickListener {
                onTransactionItemClick(categoryTransaction)
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM_VIEW_HEADER) {
            HeaderViewHolder(ItemHeaderBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemCategoryTransactionBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_VIEW_HEADER) {
            (holder as HeaderViewHolder).bind()
        } else {
            val category = categoryTransactionList[position-1]
            (holder as ItemViewHolder).bind(category)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_VIEW_HEADER else ITEM_VIEW_ITEM
    }

    override fun getItemCount(): Int {
        return categoryTransactionList.size + 1
    }

    fun setDate(newList: List<CategoryTransactionDetail>) {
        categoryTransactionList = newList
        notifyDataSetChanged()
    }

    companion object {
        private const val ITEM_VIEW_HEADER = 0
        private const val ITEM_VIEW_ITEM = 1
    }
}