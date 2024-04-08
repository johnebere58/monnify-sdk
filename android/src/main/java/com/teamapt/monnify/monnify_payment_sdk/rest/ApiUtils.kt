package com.teamapt.monnify.monnify_payment_sdk.rest

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.teamapt.monnify.monnify_payment_sdk.rest.api.MonnifyAPIClient
import com.teamapt.monnify.monnify_payment_sdk.rest.interceptor.TimeoutInterceptor
import com.teamapt.monnify.monnify_payment_sdk.service.Environment
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtils {

    private val httpClientBuilder: OkHttpClient.Builder
        get() {
            val httpClient = OkHttpClient.Builder()

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(logging)
            httpClient.addInterceptor(TimeoutInterceptor())
            // httpClient.addInterceptor(MockInterceptor())
            httpClient.networkInterceptors().add(Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Connection", "keep-alive")
                chain.proceed(requestBuilder.build())
            })

            return httpClient
        }

    private val gson: Gson
        get() {
            val builder = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            return builder.setLenient().create()
        }

    fun createFrontOfficeAPI(environment: Environment): MonnifyAPIClient {
        val baseURL = environment.url
        return getRetrofitClient(baseURL).create(MonnifyAPIClient::class.java)
    }

    private fun getRetrofitClient(apiBaseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
    }

    fun getStompHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.interceptors().add(logging)

        return httpClientBuilder.build()
    }
}
