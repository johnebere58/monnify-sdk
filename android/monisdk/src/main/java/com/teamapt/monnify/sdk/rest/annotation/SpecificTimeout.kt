package com.teamapt.monnify.sdk.rest.annotation

import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SpecificTimeout(
    val duration: Int,
    val unit: TimeUnit
)
