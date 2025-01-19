package com.example.money_manager_app.base.navigation

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavController

abstract class BaseNavigatorImpl : BaseNavigator {

    override var navController: NavController? = null

    override fun bind(navController: NavController) {
        this.navController = navController
        Log.d(TAG, "bind: $navController")
    }

    override fun unbind() {
        navController = null
    }

    override fun openScreen(
        @IdRes id: Int, bundle: Bundle?
    ) {
        Log.d(TAG, "openScreen: $navController")
        navController?.navigate(id, bundle)
    }

    override fun navigateUp() = navController?.navigateUp()

    override fun currentFragmentId() = navController?.currentDestination?.id

    override fun setStartDestination(@IdRes id: Int) {
        if (navController == null) {
            return
        }
        val navGraph = navController?.graph
        navGraph?.setStartDestination(id)
        if (navGraph != null) {
            navController?.graph = navGraph
        }
    }
    companion object {
        private const val TAG = "BaseNavigatorImpl"
    }
}