package com.monnify.monnify_payment_sdk.util

import android.content.Context
import com.google.gson.Gson
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BanksProvider(private val context: Context) {

    companion object {
        private const val THIRTY_DAYS_IN_MILLIS = 2592000000
        private const val SEVEN_DAYS_IN_MILLIS = 604800000
    }

    private val sdf = SimpleDateFormat("MMM dd yyyy, h:mm", Locale.ENGLISH)

    fun getAllBanks(): String? {
        return SharedPrefUtils.readSharedSetting(context, Constants.PREF_BANKS, "")
    }

    fun getBankPaymentBanks(): String? {
        return SharedPrefUtils.readSharedSetting(context, Constants.PREF_BANK_PAYMENT_BANKS, "")
    }

    fun saveBanks(banks: List<Bank>) {
        SharedPrefUtils.saveSharedSetting(context, Constants.PREF_BANKS, Gson().toJson(banks))
        saveTimeOfBanksLoad()
    }

    fun saveBankPaymentBanks(banks: List<Bank>) {
        SharedPrefUtils.saveSharedSetting(
            context,
            Constants.PREF_BANK_PAYMENT_BANKS,
            Gson().toJson(banks)
        )
    }

    private fun saveTimeOfBanksLoad() {
        val dateFormatted = sdf.format(Calendar.getInstance().time)
        SharedPrefUtils.saveSharedSetting(
            context,
            Constants.PREF_LAST_BANK_LOAD_TIME,
            dateFormatted
        )
    }

    private fun saveTimeOfBankPaymentBanksLoad() {
        val dateFormatted = sdf.format(Calendar.getInstance().time)
        SharedPrefUtils.saveSharedSetting(
            context,
            Constants.PREF_LAST_BANK_PAYMENT_BANK_LOAD_TIME,
            dateFormatted
        )
    }

    fun banksLastLoadedMoreThanThirtyDays(): Boolean {
        val lastDateBanksLoadedString = SharedPrefUtils.readSharedSetting(
            context,
            Constants.PREF_LAST_BANK_LOAD_TIME, ""
        )

        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(lastDateBanksLoadedString!!)!!
        } catch (e: ParseException) {
            //
        }

        return (Calendar.getInstance().timeInMillis - cal.timeInMillis) > THIRTY_DAYS_IN_MILLIS
    }

    fun bankPaymentBanksLastLoadedMoreThanThreeDays(): Boolean {
        val lastDateBanksLoadedString = SharedPrefUtils.readSharedSetting(
            context,
            Constants.PREF_LAST_BANK_PAYMENT_BANK_LOAD_TIME, ""
        )

        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(lastDateBanksLoadedString!!)!!
        } catch (e: ParseException) {
            //
        }

        return (Calendar.getInstance().timeInMillis - cal.timeInMillis) > SEVEN_DAYS_IN_MILLIS
    }

    fun banksNotLoaded(): Boolean {
        return SharedPrefUtils.readSharedSetting(context, Constants.PREF_BANKS, "") == ""
    }

    fun bankPaymentBanksNotLoaded(): Boolean {
        return SharedPrefUtils.readSharedSetting(
            context,
            Constants.PREF_BANK_PAYMENT_BANKS,
            ""
        ) == ""
    }
}
