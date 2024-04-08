package com.monnify.monnify_payment_sdk.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.monnify.monnify_payment_sdk.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PaymentMethod(@DrawableRes val methodIcon: Int, @StringRes val title: Int) : Parcelable {
    CARD(R.drawable.ic_pay_with_card, R.string.pay_with_card),
    ACCOUNT_TRANSFER(R.drawable.ic_pay_with_transfer, R.string.pay_with_transfer),
    DIRECT_DEBIT(R.drawable.ic_pay_with_bank, R.string.pay_with_bank),
    USSD(R.drawable.ic_pay_with_ussd, R.string.pay_with_ussd),
    // TODO: Future enhancement
//    NQR(R.drawable.ic_pay_with_nqr, R.string.pay_with_nqr),
//    CASH(R.drawable.ic_pay_with_cash, R.string.pay_with_cash),
    PHONE_NUMBER(R.drawable.ic_pay_with_phone, R.string.pay_with_phone);


    companion object {
        private val map = values().associateBy(PaymentMethod::name)

        fun contains(value: String?): Boolean {
            if (value == null) return false
            return map.contains(value)
        }
    }
}
