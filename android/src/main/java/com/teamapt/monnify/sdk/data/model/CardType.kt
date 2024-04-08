package com.teamapt.monnify.sdk.data.model

import java.util.regex.Pattern

enum class CardType {
    UNKNOWN,
    VISA("^4[0-9]{12}(?:[0-9]{3})?$"),
    MASTERCARD("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$"),
    AMERICAN_EXPRESS("^3[47][0-9]{13}$"),
    DINERS_CLUB("^(30([0-5]|9)[0-9]{11})|(3(6|8|9)[0-9]{12})\$"),
    DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$"),
    JCB("^(?:2131|1800|35[0-9]{3})[0-9]{11}$"),
    VERVE("^((506(0|1))|(507(8|9))|(6500))[0-9]{12,15}$");

    private var pattern: Pattern? = null

    constructor() {
        this.pattern = null
    }

    constructor(pattern: String) {
        this.pattern = Pattern.compile(pattern)
    }

    companion object {

        fun detect(cardNumber: String?): CardType {
            if (cardNumber == null)
                return UNKNOWN
            val cardNumberClean =
                formatCardNumber(cardNumber)

            for (cardType in values()) {
                if (null == cardType.pattern) continue
                if (cardType.pattern!!.matcher(cardNumberClean).matches()) return cardType
            }

            return UNKNOWN
        }

        private fun formatCardNumber(cardNumber: String): String {
            return padCardNumberIfRequired(
                cardNumber
            )
        }

        private fun padCardNumberIfRequired(cardNumber: String): String {
            val minCardLength = 16

            if (cardNumber.length >= 16) {
                return cardNumber
            }

            val sb = StringBuilder()
            sb.append(cardNumber)
            while (sb.length < minCardLength - cardNumber.length) {
                sb.append('0')
            }

            return sb.toString()
        }

    }

}
