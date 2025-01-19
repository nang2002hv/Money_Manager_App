package com.example.money_manager_app.di

import com.example.money_manager_app.base.navigation.BaseNavigator
import com.example.money_manager_app.base.navigation.BaseNavigatorImpl
import com.example.money_manager_app.navigation.AppNavigation
import com.example.money_manager_app.navigation.AppNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    @ActivityScoped
    abstract fun provideBaseNavigation(navigation: BaseNavigatorImpl): BaseNavigator

    @Binds
    @ActivityScoped
    abstract fun provideAppNavigation(navigation: AppNavigationImpl): AppNavigation
}