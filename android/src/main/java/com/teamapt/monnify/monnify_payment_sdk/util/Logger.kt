package com.teamapt.monnify.monnify_payment_sdk.util

import android.util.Log
//import com.teamapt.monnify.sdk.BuildConfig

object Logger {

    private val isDebugEnabled = false;//BuildConfig.DEBUG

    fun log(o: Any, message: String?, level: LEVEL = LEVEL.DEBUG) {
        if (isDebugEnabled) {
            val simpleName = o.javaClass.simpleName
            when (level) {
                LEVEL.DEBUG -> Log.d(simpleName, message!!)
                LEVEL.INFO -> Log.i(simpleName, message!!)
                LEVEL.ERROR -> Log.e(simpleName, message!!)
                LEVEL.VERBOSE -> Log.v(simpleName, message!!)
                LEVEL.WARN -> Log.w(simpleName, message!!)
            }
        }
    }

    enum class LEVEL {
        DEBUG, INFO, ERROR, VERBOSE, WARN
    }
}