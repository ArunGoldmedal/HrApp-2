package com.goldmedal.hrapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val KEY_SAVED_AT = "key_saved_at"
private const val KEY_INTRO = "key_intro"
private const val KEY_VERSION_CODE = "key_ver_code"
private const val KEY_VERSION_NAME = "key_ver_name"
private const val KEY_USER_PWD = "key_user_password"
private const val KEY_IS_ACTIVE = "key_is_active"
private const val KEY_FORCE_UPDATE = "key_force_update"
class PreferenceProvider @Inject constructor(@ApplicationContext context: Context){



    private val appContext = context.applicationContext

    private val  preference: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastSavedAt(savedAt: String?){
        preference.edit().putString(KEY_SAVED_AT,savedAt).apply()

    }
    fun introInit(wasShown: Boolean) {
        preference.edit().putBoolean(KEY_INTRO,wasShown).apply()
    }
    fun isIntroInit(): Boolean {
        return preference.getBoolean(KEY_INTRO,false)
    }


    fun getLastSavedAt(): String?{
        return preference.getString(KEY_SAVED_AT,null)
    }


    fun saveInitialData(verCode: Int,verName: String?,isActive: Boolean,forceUpdate: Boolean){
        preference.edit()
                .putInt(KEY_VERSION_CODE,verCode)
                .putString(KEY_VERSION_NAME,verName)
                .putBoolean(KEY_IS_ACTIVE,isActive)
                .putBoolean(KEY_FORCE_UPDATE,forceUpdate)
                .apply()
    }

    fun saveVersionCode(verCode: String?){
        preference.edit().putString(KEY_VERSION_CODE,verCode)
                .apply()

    }
    fun getVersionCode(): Int?{
        return preference.getInt(KEY_VERSION_CODE,1)
    }

    fun saveUserPassword(password: String?){
        preference.edit().putString(KEY_USER_PWD,password).apply()
    }

    fun getUserPassword(): String?{
        return preference.getString(KEY_USER_PWD,null)
    }

    fun saveVersionName(verName: String?){
        preference.edit().putString(KEY_VERSION_NAME,verName).apply()
    }
    fun getVersionName(): String?{
        return preference.getString(KEY_VERSION_NAME,null)
    }


    fun saveIsUserActive(isActive: Boolean) {
        preference.edit().putBoolean(KEY_IS_ACTIVE,isActive).apply()
    }
    fun isActive(): Boolean {
        return preference.getBoolean(KEY_IS_ACTIVE,true)
    }


    fun saveForceUpdateFlag(forceUpdate: Boolean) {
        preference.edit().putBoolean(KEY_FORCE_UPDATE,forceUpdate).apply()
    }
    fun getForceUpdateFlag(): Boolean {
        return preference.getBoolean(KEY_FORCE_UPDATE,false)
    }










}