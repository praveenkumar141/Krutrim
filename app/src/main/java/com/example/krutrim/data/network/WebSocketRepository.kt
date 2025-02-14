package com.example.krutrim.data.network

import kotlinx.coroutines.flow.StateFlow

class WebSocketRepository(private val webSocketManager: WebSocketManager) {
    val messages: StateFlow<List<String>> = webSocketManager.messages
    val error: StateFlow<String?> = webSocketManager.error

    fun startWebSocket() {
        webSocketManager.connect()
    }

    fun stopWebSocket() {
        webSocketManager.disconnect()
    }
}
