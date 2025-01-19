package com.example.money_manager_app.fragment.password.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.databinding.FragmentPasswordBinding
import com.example.money_manager_app.fragment.password.viewmodel.PasswordType
import com.example.money_manager_app.fragment.password.viewmodel.PasswordViewmodel
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordFragment :
    BaseFragment<FragmentPasswordBinding, PasswordViewmodel>(R.layout.fragment_password) {

    private var numbersInput: MutableList<TextView> = mutableListOf()
    private var numberDisplay: MutableList<EditText> = mutableListOf()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun getVM(): PasswordViewmodel {
        val viewModel: PasswordViewmodel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        numbersInput = mutableListOf(
            binding.number0,
            binding.number1,
            binding.number2,
            binding.number3,
            binding.number4,
            binding.number5,
            binding.number6,
            binding.number7,
            binding.number8,
            binding.number9,
        )

        numberDisplay = mutableListOf(
            binding.pc1,
            binding.pc2,
            binding.pc3,
            binding.pc4,
            binding.pc5,
            binding.pc6,
        )

        numberDisplay.forEach { editText ->
            editText.showSoftInputOnFocus = false
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.backspace.setOnClickListener {
            getVM().deleteNumber()
        }

        numbersInput.forEachIndexed { _, numberInput ->
            numberInput.setOnClickListener {
                getVM().addNumber(numberInput.text.toString())
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().numbersEnter.observe(viewLifecycleOwner) {
            it.forEach { (index, number) ->
                if (number.isEmpty()) {
                    numberDisplay[index].setText("")
                } else {
                    numberDisplay[index].setText(number)
                }
            }
        }

        getVM().currentCursor.observe(viewLifecycleOwner) {
            numberDisplay.forEachIndexed { index, numberInput ->
                if (index == it) {
                    if (numberInput.text.isNotEmpty()) {
                        numberInput.setText("")
                    }
                    numberInput.requestFocus()
                }
            }
        }

        getVM().currentPasswordType.observe(viewLifecycleOwner) {
            when (it) {
                PasswordType.CREATE -> {
                    binding.tvInput.setText(R.string.enter_your_pass_code)
                }

                PasswordType.CONFIRM -> {
                    getVM().reset()
                    binding.tvInput.setText(R.string.confirm_your_pass_code)
                }

                PasswordType.CHECK -> {
                    binding.tvInput.setText(R.string.confirm_your_pass_code)
                }

                else -> {
                    binding.tvInput.setText(R.string.confirm_your_pass_code)
                }
            }
        }

        getVM().isPasswordCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (isCorrect) {
                if (mainViewModel.currentAccount.value == null) {
                    appNavigation.openPasswordToCreateAccountScreen()
                } else {
                    appNavigation.openPasswordToMainScreen()
                }
            } else {
                isPasswordIncorrect()

                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

                Handler(Looper.getMainLooper()).postDelayed({
                    resetNumberDisplay()
                    getVM().reset()
                }, 1000)
            }
        }

            getVM().inputPasswordCount.observe(viewLifecycleOwner) {
            if (it in 1..4) {
                Toast.makeText(requireActivity(), getString(R.string.input_count, 5 - it), Toast.LENGTH_SHORT).show()
            } else if (it > 4) {
                Toast.makeText(requireActivity(), getString(R.string.input_count, 5 - it), Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(500)
                    requireActivity().finish()
                }
            }
        }

    }

    private fun resetNumberDisplay() {
        numberDisplay.forEach {
            it.isActivated = false
        }
    }

    private fun isPasswordIncorrect() {
        numberDisplay.forEach {
            it.isActivated = true
        }
    }
}