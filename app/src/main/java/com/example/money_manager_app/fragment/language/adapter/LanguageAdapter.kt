package com.example.money_manager_app.fragment.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.databinding.ItemLanguageBinding
import com.example.money_manager_app.data.model.LanguageModel
import com.example.money_manager_app.utils.loadImage

class LanguageAdapter(
    private val languages: List<LanguageModel>,
    private val onLanguageSelected: (LanguageModel) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding, onLanguageSelected)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languages[position])
    }

    override fun getItemCount(): Int = languages.size

    inner class LanguageViewHolder(
        private val binding: ItemLanguageBinding,
        private val onLanguageSelected: (LanguageModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(languageModel: LanguageModel) {
            binding.tvLanguage.text = languageModel.languageName
            binding.ivFlag.loadImage(languageModel.flag)
            binding.ivSelect.isActivated = languageModel.isCheck

            binding.root.setOnClickListener {
                click(languageModel)
            }
        }

        private fun click(languageModel: LanguageModel) {
            onLanguageSelected(languageModel)
            languageModel.isCheck = true
            notifyItemChanged(adapterPosition, "payload")
            handleLangDisplay(languageModel)
        }

        private fun handleLangDisplay(languageModel: LanguageModel) {
            for (i in languages.indices) {
                if (languages[i].languageName != languageModel.languageName && languages[i].isCheck) {
                    languages[i].isCheck = false
                    notifyItemChanged(i, "payload")
                }
            }
        }
    }
}