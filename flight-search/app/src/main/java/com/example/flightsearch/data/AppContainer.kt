package com.example.flightsearch.data

import android.content.Context
import com.example.flightsearch.data.airport.AirportsRepository
import com.example.flightsearch.data.favorite.FavoritesRepository

interface AppContainer {
    val airportRepository: AirportsRepository
    val favoriteRepository: FavoritesRepository
}

//class AppDataContainer(private val context: Context) : AppContainer {
//    override val airportRepository: AirportsRepository by lazy {
//        OfflineAirportssRepository(AirportsDatabase.getDatabase(context).airportsDao())
//    }
//}