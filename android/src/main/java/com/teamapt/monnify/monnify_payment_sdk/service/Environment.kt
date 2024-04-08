package com.teamapt.monnify.monnify_payment_sdk.service

enum class Environment(var url: String) {
    PROD_LIVE("https://api.monnify.com"),
    PROD_SANDBOX("https://sandbox.monnify.com"),
    STAGING_LIVE("https://api.staging.monnify.com"),
    STAGING_SANDBOX("https://sandbox.staging.monnify.com"),
    PLAYGROUND_LIVE ("https://monnify.payment.engine.playground.teamapt.com"),
    PLAYGROUND_SANDBOX ("https://sandbox.monnify.payment.engine.playground.teamapt.com");
}
