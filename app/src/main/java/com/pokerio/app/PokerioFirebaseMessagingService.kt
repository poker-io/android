package com.pokerio.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pokerio.app.utils.PokerioLogger

class PokerioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        PokerioLogger.debug("Received Message from: ${message.from}")
        PokerioLogger.debug("Data: ${message.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PokerioLogger.debug("New Token: $token")
    }
}
