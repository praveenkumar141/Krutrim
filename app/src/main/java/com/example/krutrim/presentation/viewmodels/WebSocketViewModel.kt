package com.example.krutrim.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.*
import com.example.krutrim.data.network.WebSocketRepository
import com.example.krutrim.domain.models.TickerMessage
import kotlin.math.ln
import kotlin.math.pow

class WebSocketViewModel(private val webSocketManager: WebSocketRepository) : ViewModel() {
    val messages: StateFlow<List<TickerMessage>> = webSocketManager.messages
    val error: StateFlow<String?> = webSocketManager.error

    private val _perplexity = MutableStateFlow<Double>(0.0)
    val perplexity: StateFlow<Double> get() = _perplexity

    fun startWebSocket() {
        webSocketManager.startWebSocket()
    }

    fun stopWebSocket() {
        webSocketManager.stopWebSocket()
    }

    fun calculatePerplexity(probabilities: List<Double>) {
        if (probabilities.any { it !in 0.0..1.0 }) return
        val entropy = -probabilities.sumOf { p -> p * ln(p) / ln(2.0) }
        _perplexity.value = 2.0.pow(entropy)
    }

    override fun onCleared() {
        super.onCleared()
        stopWebSocket()
    }
}


