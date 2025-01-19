package com.example.money_manager_app.fragment.wallet.add_wallet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.money_manager_app.R
import com.example.money_manager_app.base.BaseBottomSheet
import com.example.money_manager_app.databinding.IconBottomSheetBinding
import com.example.money_manager_app.fragment.wallet.adapter.WalletIconAdapter

class WalletIconBottomSheetFragment(
    private val selectedIcon: Int,
    private val onIconSelected: (Int) -> Unit,
    private val icons: List<Int>
) : BaseBottomSheet<IconBottomSheetBinding>() {

    private lateinit var iconAdapter: WalletIconAdapter

    override fun getLayoutId(): Int {
        return R.layout.icon_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter
        iconAdapter = WalletIconAdapter(selectedIcon, icons, onIconSelected)

        // Set up RecyclerView
        binding.iconRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 4) // 4 columns in grid
            adapter = iconAdapter
        }

        // Populate the adapter with the icons
        iconAdapter.updateIcons(icons)

        // Set click listeners for Cancel and Done buttons
        binding.cancelButton.setOnClickListener {
            dismiss() // Close the bottom sheet
        }

        binding.saveButton.setOnClickListener {
            dismiss() // Close the bottom sheet after saving
        }
    }
}
