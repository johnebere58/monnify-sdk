package com.teamapt.monnify.sdk.util

import com.google.gson.Gson
import com.teamapt.monnify.sdk.rest.data.response.ErrorResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class ErrorUtils {

    fun getReadableErrorMessage(t: Throwable): String {
        if (t is SocketTimeoutException || t is UnknownHostException
            || t is SSLHandshakeException || t is IOException
        ) {
            return "Error. Please check your internet connection"
        } else if (t is HttpException) {

            var errorResponse = "Connection error"

            val body = t.response()?.errorBody()
            val gson = Gson()
            val adapter = gson.getAdapter(ErrorResponse::class.java)
            try {
                errorResponse = adapter.fromJson(body!!.string())
                    .responseMessage.toString()

            } catch (e: IOException) {
                Logger.log(this, e.message)
            }

            when (t.code()) {
                401 -> return "An authentication error occurred"
                404 -> return "Unable to find resource"
                in 400..499 -> return errorResponse
                in 500..599 -> return "There was a problem with the server. Pls try again later"
            }
        }

        return "Request failed. An error occurred."
    }

}
