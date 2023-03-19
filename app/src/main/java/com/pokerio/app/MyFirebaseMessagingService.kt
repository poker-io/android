package com.pokerio.app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Received Message from: ${message.from}")
        Log.d(TAG, "Data: ${message.data}")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New Token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}