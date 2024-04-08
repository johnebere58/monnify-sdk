package com.teamapt.monnify.monnify_payment_sdk.service

interface WebSocket {
    companion object {
        const val EP_CONNECTON = "/websocket/v1/notification/legacy/subscribe"
        const val EP_SUBSCRIPTION = "/transaction/"
    }
}