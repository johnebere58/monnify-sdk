package com.monnify.monnify_payment_sdk.util

abstract class DeferredFragmentTransaction {

    var contentFrameId: Int = 0
    var replacingFragment: androidx.fragment.app.Fragment? = null

    abstract fun commit()

}