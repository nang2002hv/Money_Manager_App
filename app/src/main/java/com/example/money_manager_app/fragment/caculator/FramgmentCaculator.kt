package com.example.money_manager_app.fragment.caculator

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragmentNotRequireViewModel
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.databinding.FragmentCaculatorBinding
import com.example.money_manager_app.fragment.add.view.expense.ExpenseViewModel
import com.example.money_manager_app.fragment.add.view.income.IncomeViewModel
import com.example.money_manager_app.fragment.add.view.transfer.TransferViewModel
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Locale

class FramgmentCaculator : BaseFragmentNotRequireViewModel<FragmentCaculatorBinding>(R.layout.fragment_caculator), View.OnClickListener {

    private val incomeViewModel: IncomeViewModel by activityViewModels()
    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val transferViewModel : TransferViewModel by activityViewModels()
    private val addViewModel : AddViewModel by activityViewModels()

    private var equation: String = "0"
    private lateinit var type : TransferType

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (arguments != null){
            type = arguments?.getSerializable("type") as TransferType
        } else {
            type = TransferType.Income
        }
    }

    override fun onBack() {
        super.onBack()
        findNavController().popBackStack()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.buttonClear.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
        binding.button4.setOnClickListener(this)
        binding.button5.setOnClickListener(this)
        binding.button6.setOnClickListener(this)
        binding.button7.setOnClickListener(this)
        binding.button8.setOnClickListener(this)
        binding.button9.setOnClickListener(this)
        binding.button10.setOnClickListener(this)
        binding.button11.setOnClickListener(this)
        binding.button12.setOnClickListener(this)
        binding.button13.setOnClickListener(this)
        binding.button14.setOnClickListener(this)
        binding.button15.setOnClickListener(this)
        binding.button16.setOnClickListener(this)
        binding.button17.setOnClickListener(this)
        binding.button18.setOnClickListener(this)
        binding.backArrowButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.textView14.setOnClickListener {
            try {
                val expression = ExpressionBuilder(equation).build()
                val result = expression.evaluate()
                val longResult = result.toLong()
                if (result == longResult.toDouble()) {
                    binding.total.text = String.format(Locale.US, "%d", longResult)
                    equation = result.toString()
                    setAmount(equation.toDouble())
                    findNavController().popBackStack()
                } else{
                    binding.total.text = String.format(Locale.getDefault(), "%.2f", result)
                    equation = result.toString()
                    setAmount(equation.toDouble())
                    findNavController().popBackStack()
                }


            } catch (e: Exception) {
                Log.d("Exception", " message: ${e.message}")
            }
        }
    }

    fun setAmount(result: Double) {
        when(type){
            TransferType.Income -> {
                addViewModel.setPosition(0)
                incomeViewModel.setAmount(result)
            }
            TransferType.Expense -> {
                addViewModel.setPosition(1)
                expenseViewModel.setAmount(result)
            }
            TransferType.Transfer -> {
                addViewModel.setPosition(2)
                transferViewModel.setAmount(result)
            }
        }

    }

    private fun setTotal(total: String) {
        equation  = equation + total
        if(equation.length > 1){
            if(equation[0] == '0' && equation[1] != '.') {
                equation = equation.substring(1)
            }
        }
        binding.total.text = equation
    }

    private fun clear() {
        equation = "0"
        binding.total.text ="0"
    }

    override fun onClick(v: View?) {
        when(v?.getId()){
            R.id.button_16 -> {
                setTotal("0")
            }
            R.id.button_12 -> {
                setTotal("1")
            }
            R.id.button_13 -> {
                setTotal("2")
            }
            R.id.button_14 -> {
                setTotal("3")
            }
            R.id.button_8 -> {
                setTotal("4")
            }
            R.id.button_9 -> {
                setTotal("5")
            }
            R.id.button_10 -> {
                setTotal("6")
            }
            R.id.button_4 -> {
                setTotal("7")
            }
            R.id.button_5 -> {
                setTotal("8")
            }
            R.id.button_6 -> {
                setTotal("9")
            }
            R.id.button_17 -> {
                setTotal(".")
            }
            R.id.button_clear -> {
                clear()
            }
            R.id.button_15 -> {
                setTotal("+")
            }
            R.id.button_11 -> {
                setTotal("-")
            }
            R.id.button_7 -> {
                setTotal("*")
            }
            R.id.button_3 -> {
                setTotal("/")
            }
            R.id.button_1 -> {
                setTotal("+")
            }

            R.id.button_2 -> {
                setTotal("/")
            }
            R.id.button_18 -> {
                try {
                    val expression = ExpressionBuilder(equation).build()
                    val result = expression.evaluate()
                    val longResult = result.toLong()
                    if (result == longResult.toDouble()) {
                        binding.total.text = String.format(Locale.US, "%d", longResult)
                        equation = longResult.toString()
                    } else{
                        binding.total.text = String.format(Locale.getDefault(), "%.2f", result)
                        equation = result.toString()
                    }

                } catch (e: Exception) {
                    Log.d("Exception", " message: ${e.message}")
                }

            }
        }

    }


}