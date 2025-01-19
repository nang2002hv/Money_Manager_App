package com.example.money_manager_app.fragment.selecticon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.databinding.ItemCategoryBinding

class MultipleSelectionCategoryAdapter(
    private var listCategory: List<CategoryData.Category>,
    private var clickRadioButtonIconCategory : (CategoryData.Category, List<CategoryData.Category>) -> Unit
) : RecyclerView.Adapter<MultipleSelectionCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryData.Category) {
            binding.ivIcon.setImageResource(category.id)

            binding.tvCategoryName.text = category.name
            if(category.isCheck == false){
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_no)
            }else{
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_yes)
            }
            binding.ivRadioButton.setOnClickListener {
                clickRadioButtonIconCategory(category, listCategory)
            }
            if(category.isCheck){
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_yes)
            } else {
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_no)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCategory[position])
    }

    fun setListCategory(listCategory: List<CategoryData.Category>){
        this.listCategory = listCategory
        notifyDataSetChanged()
    }
}