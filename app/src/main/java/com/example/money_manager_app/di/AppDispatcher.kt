package com.example.money_manager_app.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val myDispatcher: AppDispatchers)

enum class AppDispatchers {
    Default,
    IO,
}
