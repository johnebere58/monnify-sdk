package com.teamapt.monnify.sdk.rest.mockservice

//import com.teamapt.monnify.sdk.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import kotlin.jvm.Throws

class MockInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response
        if (false/*BuildConfig.DEBUG*/) {
            return getMockResponse(chain)
        } else {
            response = chain.proceed(chain.request())
        }
        return response
    }

    @Throws(IOException::class)
    private fun getMockResponse(chain: Interceptor.Chain): Response {
        var responseString: String? = "{}"

        val url = chain.request().url
        var urlWithoutParameters: String? =
            null
        try {
            urlWithoutParameters = getUrlWithoutParameters(url.toString())
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        assert(urlWithoutParameters != null)

        /* responseString = when (urlWithoutParameters) {
            *//*Urls.INITIALIZE_TRANSACTION ->
                SdkUtils.loadJSONFromAsset("rest/init_transaction.json")
            Urls.CARD_REQUIREMENTS ->
                SdkUtils.loadJSONFromAsset("rest/card_requirements.json")*//*
            else ->
                return chain.proceed(chain.request())
        }*/

        return chain.proceed(chain.request())

        assert(responseString != null)
        return Response.Builder()
            .code(200)
            .message(responseString!!)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_0)
            .body(
                responseString.toByteArray()
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )
            .addHeader("content-type", "application/json")
            .build()
    }

    @Throws(URISyntaxException::class)
    private fun getUrlWithoutParameters(url: String): String {
        val uri = URI(url)
        return URI(
            uri.scheme,
            uri.authority,
            uri.path, null, // Ignore the query part of the input url
            uri.fragment
        ).toString()
    }

}
