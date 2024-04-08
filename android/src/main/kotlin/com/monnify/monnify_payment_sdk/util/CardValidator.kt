package com.monnify.monnify_payment_sdk.util

import java.util.*

class CardValidator {

    companion object {

        fun verifyCardNumber(cardNumber: String?): Boolean {
            if (cardNumber == null)
                return false
            var sum = 0
            var digit: Int
            var addend: Int
            var timesTwo = false

            for (i in cardNumber.length - 1 downTo 0) {
                digit = Integer.parseInt(cardNumber.substring(i, i + 1))
                if (timesTwo) {
                    addend = digit * 2
                    if (addend > 9) {
                        addend -= 9
                    }
                } else {
                    addend = digit
                }
                sum += addend
                timesTwo = !timesTwo
            }

            val modulus = sum % 10
            return modulus == 0

        }

        fun verifyCardExpiryDate(expiryMonth: Int?, expiryYear: Int?): Boolean {
            if (expiryMonth == null || expiryYear == null || expiryMonth > 12)
                return false
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1 // Month has zero-index
            val currentYear = calendar.get(Calendar.YEAR)

            return if (currentYear == expiryYear && currentMonth <= expiryMonth) {
                true
            } else if (expiryYear > currentYear) {
                val yearDifference = expiryYear - currentYear
                val validDurationInYears = 100
                yearDifference < validDurationInYears ||
                        yearDifference == validDurationInYears && currentMonth >= expiryMonth
            } else {
                false
            }
        }

        fun verifyCardCvv(cvv: String?): Boolean {
            return (cvv?.length == 3)
        }

        fun verifyCardPin(pin: String?): Boolean {
            return (pin?.length == 4)
        }

    }

}