package com.teamapt.monnify.sdk.module

import androidx.annotation.StringRes
import com.teamapt.monnify.sdk.R

interface CommonUIFunctions {
    fun showToastMessage(message: String = "")

    fun showLoading(@StringRes messageResId: Int = R.string.empty_string)

    fun dismissLoading()
}
