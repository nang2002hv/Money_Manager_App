package com.example.money_manager_app.fragment.statistic.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.money_manager_app.R
import com.example.money_manager_app.data.model.CalendarSummary
import com.example.money_manager_app.data.model.Stats
import com.example.money_manager_app.databinding.ListStatisticBalanceBinding
import com.example.money_manager_app.databinding.ListStatisticOverviewBinding
import com.example.money_manager_app.databinding.ListStatisticPieBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class StatisticAdapter(
    private val context: Context,
    private val currentCurrencySymbol: String,
    private val onClickOverview: () -> Unit,
    private val onClickPie: () -> Unit,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),OnChartValueSelectedListener  {

    private var openingBalance : Double = 0.0
    private var endingBalance : Double =0.0
    private var pieChart: PieChart? = null
    private var title: String = "Title"
    private var summary: CalendarSummary = CalendarSummary(0.0, 0.0)
    private var pieStatsList: List<Stats> = ArrayList()

    inner class BalanceHolder(private val binding: ListStatisticBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(openingBalance: Double, endingBalance: Double, title : String) {
            binding.openingLabel.text = "${currentCurrencySymbol} ${openingBalance}"
            binding.endingLabel.text = context.getString(R.string.money_amount, currentCurrencySymbol, endingBalance)
            binding.titleLabel.text = title
        }
    }

    inner class OverviewHolder(private val binding: ListStatisticOverviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(summary: CalendarSummary) {
            binding.incomeLabel.text = context.getString(
                R.string.positive_money_amount, currentCurrencySymbol, summary.income
            )
            binding.expenseLabel.text = context.getString(
                R.string.negative_money_amount, currentCurrencySymbol, summary.expense
            )

            val total = summary.income - summary.expense
            binding.netLabel.text = if (total >= 0) {
                context.getString(R.string.positive_money_amount, currentCurrencySymbol, total)
            } else {
                context.getString(R.string.negative_money_amount, currentCurrencySymbol, -total)
            }
            binding.showMore.setOnClickListener {
                onClickOverview()
            }
        }
    }

    inner class PieHolder(private val binding: ListStatisticPieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pieStatsList: List<Stats>) {
            val pieStatViews = listOf(
                binding.pieStat1,
                binding.pieStat2,
                binding.pieStat3,
                binding.pieStat4,
                binding.pieStat5
            )
            val pieStatViewColors = listOf(
                binding.pieStatView1,
                binding.pieStatView2,
                binding.pieStatView3,
                binding.pieStatView4,
                binding.pieStatView5
            )
            val pieStatLabels = listOf(
                binding.pieStatLabel1,
                binding.pieStatLabel2,
                binding.pieStatLabel3,
                binding.pieStatLabel4,
                binding.pieStatLabel5
            )
            val pieStatPercentLabels = listOf(
                binding.pieStatPercentLabel1,
                binding.pieStatPercentLabel2,
                binding.pieStatPercentLabel3,
                binding.pieStatPercentLabel4,
                binding.pieStatPercentLabel5
            )
            if( pieStatsList.isNotEmpty()) {
                var totalAmount = 0.0
                var percent  = 0.0
                val listStats = mutableListOf<Stats>()
                if(pieStatsList.size > 4) {
                    for (i in 0 until 4) {
                        listStats.add(pieStatsList[i])
                        percent += pieStatsList[i].percent
                    }
                    for (i in 4 until pieStatsList.size) {
                        totalAmount += pieStatsList[i].amount
                    }
                    listStats.add(Stats("Other", R.color.gray, 0, totalAmount, 100-percent, 0, 0, pieStatsList[0].type, 0, false))

                    for (i in listStats.indices) {
                        pieStatViews[i].visibility = View.VISIBLE
                        val color = ContextCompat.getColor(context, listStats[i].color)
                        pieStatViewColors[i].backgroundTintList = ColorStateList.valueOf(color)
                        pieStatLabels[i].text = listStats[i].name
                        pieStatPercentLabels[i].text = context.getString(R.string.formatted_double_percentage, listStats[i].percent)
                    }
                    pieChart = binding.pieChart
                    pieChart!!.setOnChartValueSelectedListener(this@StatisticAdapter)
                    setPieChart(binding.pieChart,listStats)
                } else {
                    for (i in 0 until pieStatsList.size) {
                        pieStatViews[i].visibility = View.VISIBLE
                        val color = ContextCompat.getColor(context, pieStatsList[i].color)
                        pieStatViewColors[i].backgroundTintList = ColorStateList.valueOf(color)
                        pieStatLabels[i].text = pieStatsList[i].name
                        pieStatPercentLabels[i].text = context.getString(R.string.formatted_double_percentage, pieStatsList[i].percent)
                    }
                    pieChart = binding.pieChart
                    pieChart!!.setOnChartValueSelectedListener(this@StatisticAdapter)
                    setPieChart(binding.pieChart,pieStatsList)
                }
            } else {
                for (i in 0 until 5) {
                    pieStatViews[i].visibility = View.GONE
                }
            }
            pieChart = binding.pieChart
            pieChart!!.setOnChartValueSelectedListener(this@StatisticAdapter)
            setPieChart(binding.pieChart,pieStatsList)

            binding.showMore.setOnClickListener {
                onClickPie()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_BALANCE
            1 -> TYPE_OVERVIEW
            else -> TYPE_PIE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BALANCE -> BalanceHolder(ListStatisticBalanceBinding.inflate(inflater, parent, false))
            TYPE_OVERVIEW -> OverviewHolder(ListStatisticOverviewBinding.inflate(inflater, parent, false))
            else -> PieHolder(ListStatisticPieBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BalanceHolder -> holder.bind(openingBalance, endingBalance, title)
            is OverviewHolder -> holder.bind(summary)
            is PieHolder -> holder.bind(pieStatsList)
        }
    }

    override fun getItemCount(): Int = 3

    fun setTitle(title: String) {
        this.title = title
        notifyDataSetChanged()
    }
    fun setBalance(openingBalance: Double, endingBalance: Double) {
        this.openingBalance = openingBalance
        this.endingBalance = endingBalance
        notifyDataSetChanged()
    }

    fun setOverviewSummary(calendarSummary: CalendarSummary) {
        this.summary = calendarSummary
        notifyDataSetChanged()
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
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
    }

    companion object {
        private const val TYPE_BALANCE = 0
        private const val TYPE_OVERVIEW = 1
        private const val TYPE_PIE = 2
    }

    private fun getCenterAmount(): SpannableString {
        var totalAmount = 0.0
        for (stat in pieStatsList) {
            totalAmount += stat.amount
        }
        val  beautifyAmount = totalAmount.toString()
        val spannableString = SpannableString(context.getString(R.string.expense) + "\n" + beautifyAmount)
        spannableString.setSpan(RelativeSizeSpan(1.0f), 0, spannableString.length - beautifyAmount.length, 0)
        return spannableString
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is PieEntry) {
            val selectedLabel = e.label
            val stat = pieStatsList.find { it.name == selectedLabel }
            val totalAmount = stat?.amount ?: 0.0
            val beautifyAmount = totalAmount.toString()
            val spannableString = SpannableString( selectedLabel + "\n" + beautifyAmount)
            spannableString.setSpan(RelativeSizeSpan(1.0f), 0, spannableString.length - beautifyAmount.length, 0)
            pieChart?.centerText =  spannableString
        }
    }

    override fun onNothingSelected() {
        pieChart?.centerText = getCenterAmount()
    }

}