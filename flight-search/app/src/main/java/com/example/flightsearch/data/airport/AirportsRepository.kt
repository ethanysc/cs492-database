package com.example.flightsearch.data.airport

import kotlinx.coroutines.flow.Flow

interface AirportsRepository {
    fun searchAirports(searchString: String): Flow<List<Airport>>

    fun getFlightsByAirportId(id: Int): Flow<List<Airport>>
}