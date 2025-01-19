package com.example.money_manager_app.fragment.create_account.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.money_manager_app.R
import com.example.money_manager_app.fragment.create_account.viewmodel.CreateAccountViewModel
import com.example.money_manager_app.base.fragment.BaseFragmentNotRequireViewModel
import com.example.money_manager_app.data.model.entity.Account
import com.example.money_manager_app.data.model.entity.enums.Currency
import com.example.money_manager_app.databinding.FragmentCreateAccountDetailBinding
import com.example.money_manager_app.fragment.create_account.view.CreateAccountFragment.Companion.ARG_ADD_ACCOUNT
import com.example.money_manager_app.fragment.create_account.view.CreateAccountFragment.Companion.ARG_INIT_AMOUNT
import com.example.money_manager_app.fragment.create_account.view.CreateAccountFragment.Companion.ARG_SELECT_CURRENCY
import com.example.money_manager_app.utils.getCurrencyName
import com.example.money_manager_app.utils.getCurrencySymbol
import com.example.money_manager_app.utils.setOnSafeClickListener
import com.example.money_manager_app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountDetailFragment :
    BaseFragmentNotRequireViewModel<FragmentCreateAccountDetailBinding>(R.layout.fragment_create_account_detail) {

    private val createAccountViewModel: CreateAccountViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var viewType: Int = ARG_ADD_ACCOUNT

    companion object {
        private const val ARG_VIEW_TYPE = "ARG_VIEW_TYPE"

        fun newInstance(viewType: Int): CreateAccountDetailFragment {
            val fragment = CreateAccountDetailFragment()
            val args = Bundle()
            args.putInt(ARG_VIEW_TYPE, viewType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        viewType = arguments?.getInt(ARG_VIEW_TYPE) ?: ARG_ADD_ACCOUNT
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        when (viewType) {
            ARG_ADD_ACCOUNT -> {
                binding.tvTitle.text = getString(R.string.add_account)
                binding.tvSubtitle.text = getString(R.string.choose_a_name_for_your_account)
                binding.etAccountName.visibility = View.VISIBLE
                binding.ivThumbnail.setImageResource(R.drawable.img_add_acount)
                binding.ivThumbnailContent.setImageResource(R.drawable.ic_user)
                binding.btnNext.setOnSafeClickListener {
                    createAccountViewModel.setName(binding.etAccountName.text.toString())
                }

                binding.btnNext.isClickable = false

                binding.etAccountName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        createAccountViewModel.checkEnterName(binding.etAccountName.text.toString())
                    }
                })
            }

            ARG_SELECT_CURRENCY -> {
                binding.btnNext.isClickable = true
                binding.spinnerCurrency.visibility = View.VISIBLE
                binding.tvTitle.text = getString(R.string.select_currency)
                binding.tvSubtitle.text = getString(R.string.favorite_currency_prompt)
                binding.ivThumbnail.setImageResource(R.drawable.img_select_currency)
                binding.ivThumbnailContent.setImageResource(R.drawable.ic_money)
                val currencies = Currency.entries.map {
                    "${it.name} - ${
                        getCurrencyName(
                            requireContext(), it
                        )
                    }(${getCurrencySymbol(requireContext(), it)})"
                }
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCurrency.adapter = adapter
                binding.btnNext.setOnSafeClickListener {
                    createAccountViewModel.setCurrency(Currency.entries[binding.spinnerCurrency.selectedItemPosition])
                }

            }

            ARG_INIT_AMOUNT -> {
                binding.amountWrapper.visibility = View.VISIBLE
                binding.tvTitle.text = getString(R.string.initial_amount)
                binding.tvSubtitle.text = getString(R.string.cash_wallet_prompt)
                binding.btnNext.text = getString(R.string.done)
                binding.ivThumbnail.setImageResource(R.drawable.img_iniatial_account)
                binding.ivThumbnailContent.setImageResource(R.drawable.ic_user)
                createAccountViewModel.currency.observe(viewLifecycleOwner) {
                    binding.tvCurrency.text = getCurrencySymbol(requireContext(), it)
                }
                binding.btnNext.isClickable = true
                binding.btnNext.setOnSafeClickListener {
                    createAccountViewModel.setInitAmount(binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0)
                    mainViewModel.insertAccount(
                        Account(
                            nameAccount = createAccountViewModel.getName(),
                            currency = createAccountViewModel.currency.value!!,
                        ),
                        createAccountViewModel.getInitAmount()
                    )
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        if (viewType == ARG_ADD_ACCOUNT) {
            createAccountViewModel.isEnterName.observe(viewLifecycleOwner) {
                binding.btnNext.isActivated = it
                binding.btnNext.isClickable = it
            }
        }
    }

}