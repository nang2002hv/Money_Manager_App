package com.example.money_manager_app.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialog<BD : ViewDataBinding> : DialogFragment() {

    @LayoutRes
    abstract fun getLayoutId(): Int
    private var _binding: BD? = null
    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Access view before created or destroyed error!")
    protected val handlerCheckAnim by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inf: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inf, getLayoutId(), container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (savedState == null) {
            initView(null)

            setOnClick()

            bindingStateView()

            bindingAction()
        }
    }

    open fun setOnClick() {

    }

    open fun initView(savedInstanceState: Bundle?) {

    }

    open fun bindingStateView() {

    }

    open fun bindingAction() {

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

    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerCheckAnim.removeCallbacksAndMessages(null)
    }
}