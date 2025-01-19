package com.example.money_manager_app.fragment.detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.Transaction
import com.example.money_manager_app.data.model.entity.Debt
import com.example.money_manager_app.data.model.entity.DebtTransaction
import com.example.money_manager_app.data.model.entity.GoalTransaction
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.data.model.entity.enums.DebtActionType
import com.example.money_manager_app.data.model.entity.enums.DebtType
import com.example.money_manager_app.data.model.entity.enums.GoalInputType
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.databinding.TransactionItemBinding
import com.example.money_manager_app.utils.toFormattedTimeString

class DetailDayAdapter(
    private var listTranfer: List<Transaction>,
    private val currencySymbol: String,
    private val context: Context,
    private var wallets: List<Wallet>?,
    private val onClick: (transaction: Transaction) -> Unit
) : RecyclerView.Adapter<DetailDayAdapter.DetailDayViewHolder>() {

    inner class DetailDayViewHolder(private var binding: TransactionItemBinding) :
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
            binding.root.setOnClickListener {
                onClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailDayViewHolder {
        val binding =
            TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailDayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTranfer.size
    }

    override fun onBindViewHolder(holder: DetailDayViewHolder, position: Int) {
        holder.bind(listTranfer[position])
    }

    fun setWallets(walletList : List<Wallet>) {
        wallets = walletList
        notifyDataSetChanged()
    }

    fun setTransfers(transfers: List<Transaction>) {
        listTranfer = transfers
        notifyDataSetChanged()
    }

}