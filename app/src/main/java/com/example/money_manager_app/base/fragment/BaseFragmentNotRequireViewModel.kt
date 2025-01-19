package com.example.money_manager_app.base.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.money_manager_app.base.activity.BaseActivityNotRequireViewModel
import com.example.money_manager_app.navigation.AppNavigation
import com.example.money_manager_app.pref.AppPreferences
import javax.inject.Inject

abstract class BaseFragmentNotRequireViewModel<BD : ViewDataBinding>(@LayoutRes id: Int) :
    Fragment(id) {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var appPreferences: AppPreferences


    private var _binding: BD? = null
    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Cannot access view after view destroyed or before view creation")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)
        _binding?.lifecycleOwner = viewLifecycleOwner

        if (savedInstanceState == null) {
            onInit()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            onInit(savedInstanceState)
        }
    }

    private fun onInit(savedInstanceState: Bundle? = null) {
        initData(savedInstanceState)

        initToolbar()

        initView(savedInstanceState)

//        checkNetworkAndRetry()

        setOnBackPress()

        setOnClick()

        bindingStateView()
    }

    private fun setOnBackPress() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    open fun initToolbar() {}

    open fun initData(savedInstanceState: Bundle?) {}

    open fun onBack() {}

    open fun setOnClick() {}

    open fun initView(savedInstanceState: Bundle?) {}

    open fun bindingStateView() {
        //do nothing
    }

    fun showHideLoading(isShow: Boolean) {
        if (activity != null && activity is BaseActivityNotRequireViewModel<*>) {
            if (isShow) {
                (activity as BaseActivityNotRequireViewModel<*>?)!!.showLoading()
            } else {
                (activity as BaseActivityNotRequireViewModel<*>?)!!.hiddenLoading()
            }
        }
    }

    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

}
