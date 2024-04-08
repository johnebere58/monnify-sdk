package com.teamapt.monnify.sdk.testutil

import kotlin.random.Random

object TestUtils {

    private val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_|"
    private val ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val NUMERIC_STRING = "0123456789"

    fun getRandomAmount(upperBound: Int, decimalPlaces: Int): String {
        val dbl = Random.nextDouble() * upperBound
        return String.format("%." + decimalPlaces + "f", dbl)
    }

    fun getRandomName(): String {
        return getRandomAlphabeticStringOfLength(getRandomInteger(10)) +
                " " + getRandomAlphabeticStringOfLength(getRandomInteger(10))
    }

    fun getRandomInteger(upperBound: Int): Int {
        return (Math.random() * upperBound + 1).toInt()
    }

    fun getRandomAlphaNumericStringOfLength(lengthOfString: Int): String {
        var length = lengthOfString

        val builder = StringBuilder()
        while (length-- != 0) {
            val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
            builder.append(ALPHA_NUMERIC_STRING[character])
        }
        return builder.toString()

    }

    fun getRandomNumericStringOfLength(lengthOfString: Int): String {
        var length = lengthOfString

        val builder = StringBuilder()
        while (length-- != 0) {
            val character = (Math.random() * NUMERIC_STRING.length).toInt()
            builder.append(NUMERIC_STRING[character])
        }
        return builder.toString()

    }

    fun getRandomAlphabeticStringOfLength(lengthOfString: Int): String {
        var length = lengthOfString

        val builder = StringBuilder()
        while (length-- != 0) {
            val character = (Math.random() * ALPHA_STRING.length).toInt()
            builder.append(ALPHA_STRING[character])
        }
        return builder.toString()

    }

}
