package com.teamapt.monnify.sdk.util

import androidx.fragment.app.Fragment

abstract class DeferredFragmentTransaction {

    var contentFrameId: Int = 0
    var replacingFragment: androidx.fragment.app.Fragment? = null

    abstract fun commit()

}