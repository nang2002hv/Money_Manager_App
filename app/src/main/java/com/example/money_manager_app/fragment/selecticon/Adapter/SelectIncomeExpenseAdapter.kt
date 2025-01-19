package com.example.money_manager_app.fragment.selecticon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.CategoryData
import com.example.money_manager_app.databinding.ItemCategoryBinding


class SelectIncomeExpenseAdapter(
    private var listCategory: List<CategoryData.Category>,
    private var clickRadioButtonIconCategory : (CategoryData.Category) -> Unit
) :
    RecyclerView.Adapter<SelectIncomeExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryData.Category) {
            binding.tvCategoryName.text = category.name
            val link = category.icon
            val resId = binding.root.context.resources.getIdentifier(link, "drawable", binding.root.context.packageName)

            if (resId != 0) {
                binding.ivIcon.setImageResource(resId)
            }

            if(category.isCheck == false){
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_no)
            }else{
                binding.ivRadioButton.setImageResource(R.drawable.radio_button_yes)
            }

            binding.ivRadioButton.setOnClickListener {
                clickRadioButtonIconCategory(category)
                if(category.isCheck == false){
                    binding.ivRadioButton.setImageResource(R.drawable.radio_button_yes)
                }
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

    fun updateData(listCategory: List<CategoryData.Category>){
        this.listCategory = listCategory
        notifyDataSetChanged()
    }

}
