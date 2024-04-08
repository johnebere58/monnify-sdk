package com.teamapt.monnify.monnify_payment_sdk.util

import android.util.Log
import com.google.gson.Gson
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.ErrorResponse
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

fun Throwable.isServerError(): Boolean {
    return if (this is HttpException && response() != null) {
        response()!!.code() >= 500 || response()!!.code() == 404
    } else
        false
}

fun Throwable.isAuthenticationError(): Boolean {
    return if (this is HttpException && this.response() != null) {
        response()!!.code() == 401
    } else
        false
}

fun Throwable.isInternetConnectionError(): Boolean {
    return this is NoConnectivityException || this is SocketTimeoutException || this is IOException
}

fun Throwable.errorResponse(): ErrorResponse? {

    if (this is HttpException) {

        val body = response()?.errorBody()
        val adapter = Gson().getAdapter(ErrorResponse::class.java)
        try {
            val errorString = body?.string() ?: ""
            return adapter.fromJson(errorString)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}

fun Throwable.readableErrorMessage(): String {

    if (this is HttpException) {

        if (this.isServerError()) {
            return "Service unavailable at the moment, please contact support."
        } else {

            val body = response()?.errorBody()
            val gson = Gson()

            val adapter = gson.getAdapter(ErrorResponse::class.java)

            try {
                val errorString = body?.string() ?: ""
                val errorResponse = adapter.fromJson(errorString)

                val message = errorResponse.responseMessage
                    ?: "An unexpected error occurred, please try again later."

                Log.d("Throwable", errorResponse.toString())

                return if (message != "null") {
                    Log.d("Throwable", "Message != null $message")
                    message
                } else {
                    Log.d("Throwable", "Message == null $message")

                    "An unexpected error occurred, please try again."
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return "An unexpected error occurred, please try again."
            }
        }

    } else if (this.isInternetConnectionError()) {
        return "We are unable to reach the server at this time." +
                " Please confirm your internet connection and try again later."
    } else {
        return "An unexpected error occurred, please try again."
    }
}