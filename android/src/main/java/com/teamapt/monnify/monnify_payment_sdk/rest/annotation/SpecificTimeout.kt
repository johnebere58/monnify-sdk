package com.teamapt.monnify.monnify_payment_sdk.rest.annotation

import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SpecificTimeout(
    val duration: Int,
    val unit: TimeUnit
)
