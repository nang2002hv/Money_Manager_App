package com.example.money_manager_app.fragment.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.databinding.ItemCategorySearchBinding

class IconCategoryAdapter(
    private val categories: List<CategoryData.Category>,
    private val onItemSelected: (CategoryData.Category) -> Unit
) : RecyclerView.Adapter<IconCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategorySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind (category: CategoryData.Category){
                binding.iconName.text = category.name
                var link = category.icon
                binding.iconImage.setImageResource(binding.root.context.resources.getIdentifier(link, "drawable", binding.root.context.packageName))
                binding.root.setOnClickListener {
                    onItemSelected(category)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategorySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }


}

