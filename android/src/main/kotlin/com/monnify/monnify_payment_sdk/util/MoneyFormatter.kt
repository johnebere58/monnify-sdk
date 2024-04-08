package com.monnify.monnify_payment_sdk.util

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


object MoneyFormatter {

    val FORMATTER = DecimalFormat("###,###,###,###,###,###,##0.00", symbols)

    private val symbols: DecimalFormatSymbols
        get() {
            val symbols = DecimalFormatSymbols(Locale.getDefault())
            symbols.decimalSeparator = '.'
            symbols.groupingSeparator = ','
            return symbols
        }

    fun format(amount: Double): String {
        val formatted = FORMATTER.format(amount)
        return String.format("₦ %s", formatted)
    }

    fun format(amount: Double, includeCurrencySymbol: Boolean): String {
        return if (includeCurrencySymbol) {
            format(amount)
        } else FORMATTER.format(amount)
    }

    fun format(amount: Double, includeCurrencySymbol: Boolean, includeKoboPoints: Boolean): String {
        if (includeCurrencySymbol) {
            return format(amount)
        }
        var formatted = FORMATTER.format(amount)
        if (!includeKoboPoints) {
            formatted = formatted.substring(0, formatted.length - 3)
        }
        return formatted
    }

    @JvmOverloads
    fun format(
        amount: BigDecimal,
        currencyCode: String,
        includeKoboPoints: Boolean = false
    ): String {
        var amountString = FORMATTER.format(amount)

        if (includeKoboPoints) {
            amountString = amountString.substring(0, amountString.length - 3)
        }

        return String.format("%s %s", currencyCode, amountString)
    }
}
