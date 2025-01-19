package com.example.money_manager_app.fragment.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.databinding.IconItemBinding

class WalletIconAdapter(
    private var selectedIcon: Int,
    private var icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<WalletIconAdapter.WalletIconViewHolder>() {

    private var currentSelectedIcon: Int? = selectedIcon

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletIconViewHolder {
        val binding = IconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletIconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletIconViewHolder, position: Int) {
        holder.bind(icons[position], icons[position] == currentSelectedIcon)
    }

    override fun getItemCount(): Int = icons.size

    fun updateIcons(newIcons: List<Int>) {
        icons = newIcons
        notifyDataSetChanged()
    }

    fun updateSelectedIcon(newSelectedIcon: Int) {
        val oldSelectedIcon = currentSelectedIcon
        currentSelectedIcon = newSelectedIcon

        // Update the UI for the previously selected and newly selected icons
        val oldPosition = icons.indexOfFirst { it == oldSelectedIcon }
        val newPosition = icons.indexOfFirst { it == newSelectedIcon }

        if (oldPosition != -1) notifyItemChanged(oldPosition)
        if (newPosition != -1) notifyItemChanged(newPosition)
    }

    inner class WalletIconViewHolder(
        private val binding: IconItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(iconItem: Int, isSelected: Boolean) {
            binding.iconImageView.setImageResource(iconItem)
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.selected_icon_background else 0
            )
            binding.root.setOnClickListener {
                onIconSelected(iconItem)
                updateSelectedIcon(iconItem)
            }
        }
    }
}
