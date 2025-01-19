package com.example.money_manager_app.fragment.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.WalletItem
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.databinding.AddNewWalletItemBinding
import com.example.money_manager_app.databinding.WalletItemBinding

class WalletAdapter(
    private val context: Context,
    private val currentCurrencySymbol: String,
    private val onWalletItemClick: (Wallet) -> Unit,
    private val onAddWalletClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var wallets: List<WalletItem> = emptyList()

    inner class WalletViewHolder(private val binding: WalletItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(walletItem: WalletItem) {
            val wallet = walletItem.wallet
            val balance = walletItem.endingAmount
            binding.walletName.text = wallet.name
            binding.walletBalance.text = context.getString(
                R.string.money_amount, currentCurrencySymbol, balance
            )
            binding.walletIcon.setImageResource(wallet.iconId)
            binding.root.setOnClickListener { onWalletItemClick(wallet) }
        }
    }

    inner class AddWalletViewHolder(binding: AddNewWalletItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onAddWalletClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_WALLET) {
            WalletViewHolder(WalletItemBinding.inflate(inflater, parent, false))
        } else {
            AddWalletViewHolder(AddNewWalletItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount(): Int {
        // Add one more item for the "Add Wallet" view
        return wallets.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_WALLET) {
            val wallet = wallets[position]
            (holder as WalletViewHolder).bind(wallet)
        } else if (holder is AddWalletViewHolder) {
            // No binding required for the "Add Wallet" button
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Show the "Add Wallet" view as the last item
        return if (position == wallets.size) TYPE_ADD_WALLET else TYPE_WALLET
    }

    fun setWallets(newWallets: List<WalletItem>) {
        val diffCallback = WalletDiffCallback(wallets, newWallets)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        wallets = newWallets
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        private const val TYPE_WALLET = 0
        private const val TYPE_ADD_WALLET = 1
    }
}

class WalletDiffCallback(
    private val oldList: List<WalletItem>,
    private val newList: List<WalletItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].wallet.id == newList[newItemPosition].wallet.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
