package com.example.money_manager_app.di

import android.content.Context
import androidx.room.Room
import com.example.money_manager_app.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Dispatcher(AppDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(AppDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(appDatabase: AppDatabase) = appDatabase.accountDao()

    @Provides
    @Singleton
    fun provideTransferDao(appDatabase: AppDatabase) = appDatabase.transferDao()

    @Provides
    @Singleton
    fun provideWalletDao(appDatabase: AppDatabase) = appDatabase.walletDao()

    @Provides
    @Singleton
    fun provideDebtDao(appDatabase: AppDatabase) = appDatabase.debtDao()

    @Provides
    @Singleton
    fun provideDebtTransactionDao(appDatabase: AppDatabase) = appDatabase.debtTransactionDao()

    @Provides
    @Singleton
    fun provideBudgetDao(appDatabase: AppDatabase) = appDatabase.budgetDao()

    @Provides
    @Singleton
    fun provideGoalDao(appDatabase: AppDatabase) = appDatabase.goalDao()

    @Provides
    @Singleton
    fun provideGoalTransactionDao(appDatabase: AppDatabase) = appDatabase.goalTransactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase) = appDatabase.categoryDao()
}