package com.example.money_manager_app.di

import com.example.money_manager_app.pref.AppPreferences
import com.example.money_manager_app.pref.AppPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @Singleton
    abstract fun bindAppPreferences(
        appPreferencesImpl: AppPreferencesImpl
    ): AppPreferences
}