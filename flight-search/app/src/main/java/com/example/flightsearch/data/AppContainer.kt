package com.example.flightsearch.data

import android.content.Context

interface AppContainer {
    val airportRepository: AirportsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val airportRepository: AirportsRepository by lazy {
        OfflineAirportssRepository(AirportsDatabase.getDatabase(context).airportsDao())
    }
}
