package com.example.krutrim.di

import com.example.krutrim.data.network.WebSocketManager
import com.example.krutrim.data.network.WebSocketRepository
import com.example.krutrim.presentation.viewmodels.WebSocketViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { WebSocketManager("wss://ws-feed.exchange.coinbase.com") }
    single { WebSocketRepository(get()) }
    viewModel { WebSocketViewModel(get()) }
}
