package com.example.money_manager_app.fragment.structure.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.Stats
import com.example.money_manager_app.databinding.ListStatisticPieHeaderBinding
import com.example.money_manager_app.databinding.ListStatisticPieItemBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class StructApdapter(
    private val context: Context,
    private val currentCurrencySymbol: String
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() , OnChartValueSelectedListener {

    private var pieChart: PieChart? = null
    private var pieStatsList: List<Stats> = ArrayList()


    inner class PieHolder(private val binding: ListStatisticPieHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pieStatsList: List<Stats>) {
            pieChart = binding.pieChart
            pieChart!!.setOnChartValueSelectedListener(this@StructApdapter)
            setPieChart(binding.pieChart,pieStatsList)
        }
    }

    inner class ItemStatisticPieItemHolder(private val binding: ListStatisticPieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stats : Stats) {
            binding.imageView.setImageResource(stats.categoryDefault)
            binding.nameLabel.text = stats.name
            if (stats.isDefault){
                binding.amountLabel.text = context.getString(
                    R.string.positive_money_amount, currentCurrencySymbol, stats.amount)
                binding.amountLabel.setTextColor(ContextCompat.getColor(context, R.color.Brand_Primary))
            } else {
                binding.amountLabel.text = context.getString(
                    R.string.negative_money_amount, currentCurrencySymbol, stats.amount)
                binding.amountLabel.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            binding.transLabel.text = "${stats.trans} " + context.getString(R.string.transaction)
            binding.detailLabel.text = context.getString(R.string.formatted_double_percentage, stats.percent)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_PIE
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PIE -> PieHolder(ListStatisticPieHeaderBinding.inflate(inflater, parent, false))
            else -> ItemStatisticPieItemHolder(ListStatisticPieItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                if (holder is PieHolder) {
                    holder.bind(pieStatsList)
                }
            }
            1 -> {
                if(pieStatsList.isNotEmpty()) {
                    if (holder is ItemStatisticPieItemHolder) {
                        holder.bind(pieStatsList[position - 1])
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return pieStatsList.size + 1
    }

    fun setPieStatsList(list: List<Stats>) {
        this.pieStatsList = list
        notifyDataSetChanged()
    }

    private fun setPieChart(pieChart: PieChart, statsList : List<Stats>) {
        pieChart.highlightValue(null)
        pieChart.centerText = getCenterAmount()
        pieChart.setUsePercentValues(true)
        pieChart.transparentCircleRadius = 0.0f
        pieChart.holeRadius = 70.0f
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = false
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        for(stats in statsList) {
            val percentFormatter = stats.percent.toFloat()
            entries.add(PieEntry(percentFormatter, stats.name))
            val color = ContextCompat.getColor(context, stats.color)
            colors.add(color)
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val data = PieData(dataSet)
        data.setDrawValues(false)
        data.setValueTextSize(14.0f)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
    }
    private fun getCenterAmount(): SpannableString {
        var totalAmount: Double = 0.0
        for (stat in pieStatsList) {
            totalAmount += stat.amount
        }
        val beautifyAmount = context.getString(R.string.negative_money_amount, currentCurrencySymbol, totalAmount)
        val spannableString = SpannableString(context.getString(R.string.expense) + "\n" + beautifyAmount)
        spannableString.setSpan(RelativeSizeSpan(1.0f), 0, spannableString.length - beautifyAmount.length, 0)
        return spannableString
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is PieEntry) {
            val selectedLabel = e.label

            val stat = pieStatsList.find { it.name == selectedLabel }
            val totalAmount = stat?.amount ?: 0.0
            val beautifyAmount = context.getString(R.string.negative_money_amount, currentCurrencySymbol, totalAmount)
            val spannableString = SpannableString( selectedLabel + "\n" + beautifyAmount)
            spannableString.setSpan(RelativeSizeSpan(1.0f), 0, spannableString.length - beautifyAmount.length, 0)
            pieChart?.centerText =  spannableString
        }
    }

    override fun onNothingSelected() {
        pieChart?.centerText = getCenterAmount()
    }

    companion object {
        private const val TYPE_PIE = 0
        private const val TYPE_ITEM = 1
    }

}