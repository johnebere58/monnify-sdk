package com.monnify.monnify_payment_sdk

import android.app.Activity
import android.content.Intent
import androidx.annotation.Keep
import com.monnify.monnify_payment_sdk.data.model.TransactionDetails
import com.monnify.monnify_payment_sdk.module.view.activity.SdkActivity
import com.monnify.monnify_payment_sdk.service.ApplicationMode
import com.monnify.monnify_payment_sdk.service.Environment
import com.monnify.monnify_payment_sdk.util.Constants
import com.monnify.monnify_payment_sdk.util.Logger

class Monnify {

    lateinit var KEY_RESULT: String

    var paymentStatus = MonnifyTransactionResponse()

    private lateinit var applicationMode: ApplicationMode
    private lateinit var apiKey: String
    private lateinit var contractCode: String

    init {
        if (monnify != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

    @Keep
    companion object {
        @Volatile
        private var monnify: Monnify? = null

        val instance: Monnify
            get() {
                if (monnify == null) {
                    synchronized(Monnify::class.java) {
                        if (monnify == null)
                            monnify = Monnify()
                    }
                }
                return monnify!!
            }
    }

    fun setApplicationMode(applicationMode: ApplicationMode) {
        this.applicationMode = applicationMode
    }

    fun getEnvironment(): Environment {

        val isProduction = true

        return when (applicationMode) {
            ApplicationMode.LIVE -> {
                if (isProduction) Environment.PROD_LIVE else Environment.PROD_SANDBOX
            }
            ApplicationMode.STAGING -> {
                if (isProduction) Environment.STAGING_LIVE else Environment.STAGING_SANDBOX
            }
            ApplicationMode.TEST -> {
                if (isProduction) Environment.PLAYGROUND_LIVE else Environment.PLAYGROUND_SANDBOX
            }
        }
    }

    fun setApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    fun setContractCode(contractCode: String) {
        this.contractCode = contractCode
    }

    fun getApplicationMode(): ApplicationMode {
        return applicationMode
    }

    fun getApiKey(): String {
        return apiKey
    }

    fun getContractCode(): String {
        return contractCode
    }

    fun initializePayment(
        activity: Activity,
        transactionDetails: TransactionDetails,
        requestCode: Int,
        resultKey: String
    ) {

        KEY_RESULT = resultKey
        paymentStatus = MonnifyTransactionResponse()
        val intent: Intent?

        try {
            intent = Intent(activity, SdkActivity::class.java)
            intent.putExtra(Constants.TRANSACTION_DETAILS_ARGS, transactionDetails)
            activity.startActivityForResult(intent, requestCode)

        } catch (e: ClassNotFoundException) {
            Logger.log(this, e.toString())
        }
    }
}
