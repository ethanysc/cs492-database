package com.example.flightsearch.data.airport

import kotlinx.coroutines.flow.Flow

class OfflineAirportsRepository(private val airportDao: AirportDao) : AirportsRepository {
    override fun searchAirports(searchString: String): Flow<List<Airport>> = airportDao.searchAirports(searchString)

    override fun getFlightsByAirportId(id: Int): Flow<List<Airport>> = airportDao.getFlightsByAirportId(id)
}