package com.example.money_manager_app.fragment.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.AccountWithWalletItem
import com.example.money_manager_app.data.model.entity.enums.WalletType
import com.example.money_manager_app.databinding.AccountItemBinding

class AccountAdapter(
    private val context: Context,
    private val accounts: List<AccountWithWalletItem>,
    private val currentAccount: AccountWithWalletItem,
    private val isBalanceHidden : Boolean = false,
    private val onAccountSelected: (AccountWithWalletItem) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = AccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account, account.account == currentAccount.account)
    }

    override fun getItemCount(): Int {
        return if (accounts.isNotEmpty()) accounts.size else 0
    }

    inner class AccountViewHolder(private val binding: AccountItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: AccountWithWalletItem, isSelect: Boolean = false) {
            binding.accountName.text = account.account.nameAccount
            val currencySymbol = context.getString(account.account.currency.symbolRes)
            var balance = 0.0
            account.walletItems?.let {
                for (walletItem in it) {
                    if (walletItem.wallet.walletType == WalletType.GENERAL && walletItem.wallet.isExcluded == false) {
                        balance += walletItem.endingAmount
                    }
                }
            }
            binding.accountBalance.text = if(!isBalanceHidden) context.getString(R.string.money_amount, currencySymbol , balance) else context.getString(R.string.hidden_balance)
            binding.checkIcon.visibility = if (isSelect) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onAccountSelected(account)
            }
        }
    }
}
