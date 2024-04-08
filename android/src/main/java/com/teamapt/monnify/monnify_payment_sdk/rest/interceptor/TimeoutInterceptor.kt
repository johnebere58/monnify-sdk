package com.teamapt.monnify.monnify_payment_sdk.rest.interceptor

import com.teamapt.monnify.monnify_payment_sdk.rest.annotation.SpecificTimeout
import com.teamapt.monnify.monnify_payment_sdk.util.Logger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.lang.reflect.Method

class TimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val tag: Invocation? = request.tag(Invocation::class.java)
        val method: Method? = tag?.method()
        val timeout: SpecificTimeout? = method?.getAnnotation(SpecificTimeout::class.java)

        if (timeout != null) {
            return chain
                .withConnectTimeout(timeout.duration, timeout.unit)
                .withWriteTimeout(timeout.duration, timeout.unit)
                .withReadTimeout(timeout.duration, timeout.unit)
                .proceed(request)
        }

        return chain.proceed(request)
    }
}