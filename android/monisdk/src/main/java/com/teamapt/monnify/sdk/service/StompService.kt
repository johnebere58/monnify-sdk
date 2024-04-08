package com.teamapt.monnify.sdk.service

import com.teamapt.monnify.sdk.rest.ApiUtils
import io.reactivex.Flowable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
//import ua.naiksoftware.stomp.pathmatcher.ActivePathMatcher
import ua.naiksoftware.stomp.pathmatcher.SimplePathMatcher

class StompService(environment: Environment) {
    private var stompClient: StompClient

    val isStompConnected: Boolean
        get() = stompClient.isConnected

    init {
        if (restServiceInstance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
        stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            environment.url + WebSocket.EP_CONNECTON,
            null, ApiUtils.getStompHttpClient()
        )
//        stompClient.setPathMatcher(ActivePathMatcher())
        stompClient.setPathMatcher(SimplePathMatcher())

    }

    fun connectStomp(): Flowable<LifecycleEvent> {
        stompClient.withServerHeartbeat(1000).withClientHeartbeat(1000)
        stompClient.connect()

        return stompClient.lifecycle()
    }

    fun reconnect() {
        stompClient.reconnect()
    }

    fun subscribeOnStomp(transactionReference: String): Flowable<StompMessage> {
        return stompClient.topic(WebSocket.EP_SUBSCRIPTION + transactionReference)
    }

    fun disconnect() {
        if (isStompConnected)
            stompClient.disconnect()
    }

    companion object {
        @Volatile
        private var restServiceInstance: StompService? = null

        fun getInstance(environment: Environment): StompService? {
            if (restServiceInstance == null) {
                synchronized(StompService::class.java) {
                    if (restServiceInstance == null)
                        restServiceInstance = StompService(environment)
                }
            }
            return restServiceInstance

        }

        fun destroySingleton() {
            restServiceInstance = null
        }

    }
//
//    private inner class StompReconnectTask: AsyncTask<Unit, Unit, Boolean>() {
//
//        override fun doInBackground(vararg params: Unit?): Boolean {
//
//            Thread.sleep(1500)
//            while (!SdkUtils.isNetworkAvailable())
//            // Keep looping until there is a network connection
//                Thread.sleep(3500)
//
//            return true
//        }
//
//        override fun onPostExecute(doReconnect: Boolean) {
//            super.onPostExecute(doReconnect)
//            if (doReconnect)
//                reconnectStomp()
//        }
//
//    }

}
