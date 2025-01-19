package com.example.money_manager_app.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.money_manager_app.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context
) : AppPreferences {

    companion object {
        const val PREF_PARAM_IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"
        const val PREF_PARAM_LANGUAGE = "LANGUAGE"
        const val PREF_PARAM_PASSWORD = "PASSWORD"
    }

    private var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val mPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        Constants.PREF_FILE_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun put(key: String, value: String) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun get(key: String): String? {
        return mPrefs.getString(key, null)
    }

    override fun put(key: String, value: Int) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    override fun get(key: String, default: Long) = mPrefs.getLong(key, default)

    override fun put(key: String, value: Long) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun get(key: String, default: Int) = mPrefs.getInt(key, default)

    override fun put(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun get(key: String, default: Boolean) = mPrefs.getBoolean(key, default)

    override fun clear() {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.clear()
        editor.apply()
    }

    override fun remove(key: String) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun isFirstTimeLaunch() = get(PREF_PARAM_IS_FIRST_TIME_LAUNCH, true)

    override fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        put(PREF_PARAM_IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch)
    }

    override fun getLanguage(): String = get(PREF_PARAM_LANGUAGE) ?: "en"

    override fun setLanguage(language: String) {
        put(PREF_PARAM_LANGUAGE, language)
    }

    override fun getPassword(): String = get(PREF_PARAM_PASSWORD) ?: ""

    override fun setPassword(password: String) {
        put(PREF_PARAM_PASSWORD, password)
    }

    override fun setCurrentAccount(accountId: Long) {
        put(Constants.Preferences.PREF_PARAM_CURRENT_ACCOUNT, accountId)
    }

    override fun getCurrentAccount(): Long = get(Constants.Preferences.PREF_PARAM_CURRENT_ACCOUNT, 0L)

    override fun getHiddenBalance(): Boolean = get(Constants.Preferences.PREF_HIDDEN_BALANCE, false)

    override fun setHiddenBalance(hidden: Boolean){
        put(Constants.Preferences.PREF_HIDDEN_BALANCE, hidden)
    }

}