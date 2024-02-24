package com.example.flightsearch.data

import android.content.Context
import com.example.flightsearch.data.airport.AirportsRepository
import com.example.flightsearch.data.airport.OfflineAirportsRepository
import com.example.flightsearch.data.favorite.FavoritesRepository
import com.example.flightsearch.data.favorite.OfflineFavoritesRepository

interface AppContainer {
    val airportsRepository: AirportsRepository
    val favoritesRepository: FavoritesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val airportsRepository: AirportsRepository by lazy {
        OfflineAirportsRepository(FlightDatabase.getDatabase(context).airportsDao())
    }
    override val favoritesRepository: FavoritesRepository by lazy {
        OfflineFavoritesRepository(FlightDatabase.getDatabase(context).favoritesDao())
    }
}