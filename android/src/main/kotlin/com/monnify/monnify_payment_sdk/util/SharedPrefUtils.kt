package com.monnify.monnify_payment_sdk.util

import android.content.Context
import com.monnify.monnify_payment_sdk.util.Constants.Companion.PREFERENCES_FILE

object SharedPrefUtils {

    fun readSharedSetting(ctx: Context, settingName: String, defaultValue: String): String? {
        val sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(settingName, defaultValue)
    }

    fun saveSharedSetting(ctx: Context, settingName: String, settingValue: String?) {
        val sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(settingName, settingValue)
        editor.apply()
    }

    fun persistMerchantCredentials(apiKey: String,
                                   contractCode: String,
                                   applicationMode: String) {

    }
}
