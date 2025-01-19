package com.example.money_manager_app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.TransactionListItem
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.databinding.DateHeaderItemBinding
import com.example.money_manager_app.databinding.TransactionItemBinding
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.utils.toFormattedTimeString

class TransactionAdapter(
    private val context: Context,
    private val currencySymbol: String,
    private val wallets: List<Wallet>?,
    private val categories: List<Category>? = null,
    private val onTransactionClick: (Transaction) -> Unit = {}
) : ListAdapter<TransactionListItem, RecyclerView.ViewHolder>(TransactionListItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_DATE_HEADER = 0
        private const val VIEW_TYPE_TRANSACTION_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionListItem.DateHeader -> VIEW_TYPE_DATE_HEADER
            is TransactionListItem.TransactionItem -> VIEW_TYPE_TRANSACTION_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_DATE_HEADER) {
            val binding =
                DateHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateHeaderViewHolder(binding)
        } else {
            val binding =
                TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TransactionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TransactionListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is TransactionListItem.TransactionItem -> (holder as TransactionViewHolder).bind(
                item.transaction
            )
        }
    }

    inner class DateHeaderViewHolder(private val binding: DateHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: TransactionListItem.DateHeader) {
            binding.apply {
                dayOfWeekLabel.text = header.dayOfWeek
                dayOfMonthLabel.text = header.dayOfMonth
                monthYearLabel.text = header.monthYear
                totalAmountLabel.text = if (header.total >= 0) {
                    context.getString(R.string.positive_money_amount, currencySymbol, header.total)
                } else {
                    context.getString(
                        R.string.negative_money_amount, currencySymbol, header.total * -1
                    )
                }
            }
        }
    }

    inner class TransactionViewHolder(private val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            when (transaction) {
                is GoalTransaction -> {
                    binding.transactionTypeImageView.setImageResource(transaction.iconId)
                    binding.transactionTypeTextView.text = context.getString(
                        R.string.goal_transaction_name,
                        transaction.type.toString(),
                        transaction.name
                    )
                    binding.transactionAmount.text =
                        if (transaction.type == GoalInputType.DEPOSIT) {
                            context.getString(
                                R.string.negative_money_amount, currencySymbol, transaction.amount
                            )
                        } else {
                            context.getString(
                                R.string.positive_money_amount, currencySymbol, transaction.amount
                            )
                        }
                }

                is Debt -> {
                    binding.transactionTypeImageView.setImageResource(transaction.iconId)
                    if (transaction.type == DebtType.PAYABLE) {
                        binding.transactionAmount.text = context.getString(
                            R.string.positive_money_amount, currencySymbol, transaction.amount
                        )
                        binding.transactionAmount.setTextColor(context.getColor(R.color.color_1))
                        binding.transactionTypeTextView.text =
                            context.getString(R.string.i_owe_s, transaction.name)
                    } else {
                        binding.transactionAmount.text = context.getString(
                            R.string.negative_money_amount, currencySymbol, transaction.amount
                        )
                        binding.transactionAmount.setTextColor(context.getColor(R.color.color_17))
                        binding.transactionTypeTextView.text =
                            context.getString(R.string.i_lend_s, transaction.name)
                    }
                }

                is DebtTransaction -> {
                    binding.transactionTypeImageView.setImageResource(transaction.iconId)
                    binding.transactionTypeTextView.text = context.getString(
                        R.string.debt_transaction_name,
                        transaction.action.toString(),
                        transaction.name
                    )
                    if (transaction.action == DebtActionType.DEBT_INCREASE
                        || transaction.action == DebtActionType.DEBT_COLLECTION
                        || transaction.action == DebtActionType.LOAN_INTEREST) {
                        binding.transactionAmount.text = context.getString(
                            R.string.positive_money_amount, currencySymbol, transaction.amount
                        )
                        binding.transactionAmount.setTextColor(context.getColor(R.color.color_1))
                    } else {
                        binding.transactionAmount.text = context.getString(
                            R.string.negative_money_amount, currencySymbol, transaction.amount
                        )
                        binding.transactionAmount.setTextColor(context.getColor(R.color.color_17))
                    }
                }

                is Transfer -> {
                    binding.transactionTypeTextView.text = transaction.description
                    if(transaction.typeOfExpenditure == TransferType.Expense || transaction.typeOfExpenditure == TransferType.Income) {
                        binding.transactionTypeImageView.setImageResource(transaction.iconId ?: 0)
                    } else {
                        binding.transactionTypeImageView.setImageResource(R.drawable.transfer)
                    }

                    when (transaction.typeOfExpenditure) {
                        TransferType.Expense -> {
                            binding.transactionAmount.text = context.getString(
                                R.string.negative_money_amount, currencySymbol, transaction.amount
                            )
                            binding.transactionAmount.setTextColor(context.getColor(R.color.red))
                        }

                        TransferType.Income -> {
                            binding.transactionAmount.text = context.getString(
                                R.string.positive_money_amount, currencySymbol, transaction.amount
                            )
                            binding.transactionAmount.setTextColor(context.getColor(R.color.color_2))
                        }

                        else -> {
                            binding.transactionAmount.text = context.getString(
                                R.string.money_amount, currencySymbol, transaction.amount
                            )
                        }
                    }
                }

                else -> {
                    binding.transactionTypeTextView.text = transaction.name
                    binding.transactionAmount.text =
                        context.getString(R.string.money_amount, currencySymbol, transaction.amount)
                }
            }
            binding.transactionTime.text = transaction.time.toFormattedTimeString()
            binding.walletName.text = wallets?.find { it.id == transaction.walletId }?.name ?: ""
            binding.root.setOnSafeClickListener {
                onTransactionClick(transaction)
            }
        }
    }
}

class TransactionListItemDiffCallback : DiffUtil.ItemCallback<TransactionListItem>() {
    override fun areItemsTheSame(
        oldItem: TransactionListItem, newItem: TransactionListItem
    ): Boolean {
        return if (oldItem is TransactionListItem.DateHeader && newItem is TransactionListItem.DateHeader) {
            oldItem.dayOfMonth == newItem.dayOfMonth && oldItem.monthYear == newItem.monthYear
        } else if (oldItem is TransactionListItem.TransactionItem && newItem is TransactionListItem.TransactionItem) {
            oldItem.transaction.id == newItem.transaction.id
        } else {
            false
        }
    }

    override fun areContentsTheSame(
        oldItem: TransactionListItem, newItem: TransactionListItem
    ): Boolean {
        return oldItem == newItem
    }
}