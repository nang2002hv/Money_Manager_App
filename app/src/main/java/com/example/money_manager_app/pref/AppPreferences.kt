package com.example.money_manager_app.pref

interface AppPreferences {

    fun get(key: String): String?

    fun get(key: String, default: Int): Int

    fun get(key: String, default: Boolean): Boolean

    fun put(key: String, value: String)

    fun put(key: String, value: Int)

    fun put(key: String, value: Boolean)

    fun put(key: String, value: Long)

    fun get(key: String, default: Long): Long

    fun clear()

    fun remove(key: String)

    fun isFirstTimeLaunch() : Boolean

    fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean)

    fun getLanguage() : String

    fun setLanguage(language: String)

    fun getPassword() : String

    fun setPassword(password: String)

    fun setCurrentAccount(accountId: Long)

    fun getCurrentAccount() : Long

    fun getHiddenBalance() : Boolean

    fun setHiddenBalance(hidden: Boolean)
}