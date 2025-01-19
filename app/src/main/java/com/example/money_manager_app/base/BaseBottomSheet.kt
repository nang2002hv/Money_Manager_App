package com.example.money_manager_app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.money_manager_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<BD : ViewDataBinding> :
    BottomSheetDialogFragment() {

    @LayoutRes
    abstract fun getLayoutId(): Int
    private var _binding: BD? = null

    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Access view before created or destroyed error!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            initView(savedInstanceState)

            setOnClick()

            bindingStateView()

            bindingAction()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            initView(savedInstanceState)

            setOnClick()

            bindingStateView()

            bindingAction()
        }
    }

    open fun setOnClick() {

    }

    open fun initView(savedInstanceState: Bundle?) {

    }

    open fun bindingAction() {

    }

    open fun bindingStateView() {

    }



    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

}