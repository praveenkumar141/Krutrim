package com.example.krutrim.di

import com.example.krutrim.data.local.AppDatabase
import com.example.krutrim.data.network.WebSocketManager
import com.example.krutrim.data.network.WebSocketRepository
import com.example.krutrim.presentation.viewmodels.WebSocketViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Provide the AppDatabase
    single {
        AppDatabase.getDatabase(get())
    }

    // Provide the TickerMessageDao
    single {
        get<AppDatabase>().tickerMessageDao()
    }

    // Provide the WebSocketManager with the required TickerMessageDao
    single {
        WebSocketManager("wss://ws-feed.exchange.coinbase.com", get())
    }

    // Provide the WebSocketRepository with the required WebSocketManager
    single {
        WebSocketRepository(get())
    }

    // Provide the WebSocketViewModel with the required WebSocketRepository
    viewModel {
        WebSocketViewModel(get())
    }
}