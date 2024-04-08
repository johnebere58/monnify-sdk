package com.teamapt.monnify.sdk.service

interface WebSocket {
    companion object {
        const val EP_CONNECTON = "/websocket/v1/notification/legacy/subscribe"
        const val EP_SUBSCRIPTION = "/transaction/"
    }
}