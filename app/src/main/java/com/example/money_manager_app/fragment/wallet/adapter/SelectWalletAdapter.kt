package com.example.money_manager_app.fragment.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.entity.Wallet
import com.example.money_manager_app.databinding.CustomeIconWalletBinding


class SelectWalletAdapter(private var listWallet: List<Wallet>,
                          private val onItemClick: (Wallet) -> Unit,
                          private val context: Context,
                          private val currencySymbol: String,
    ) : RecyclerView.Adapter<SelectWalletAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CustomeIconWalletBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind (wallet: Wallet){
                binding.tvWallet.text = wallet.name.toString()
                binding.ivItem.setImageResource(wallet.iconId)
                binding.tvMoney.text = context.getString(R.string.money_amount, currencySymbol, wallet.amount)
                binding.root.setOnClickListener {
                    onItemClick(wallet)
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomeIconWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listWallet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listWallet[position])
    }

    fun setUpdateDataWallet(wallets: List<Wallet>){
        listWallet = wallets
        notifyDataSetChanged()
    }
}