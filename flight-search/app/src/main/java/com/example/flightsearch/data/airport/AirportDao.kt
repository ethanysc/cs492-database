package com.example.flightsearch.data.airport

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * from airports " +
            "WHERE name LIKE '%:searchString' OR " +
            "iata_code LIKE '%:searchString' " +
            "ORDER BY iata_code ASC")
    fun searchAirports(searchString: String): Flow<List<Airport>>

    @Query("SELECT * from airports " +
            "WHERE id != :id " +
            "ORDER by iata_code ASC")
    fun getFlightsByAirportId(id: Int): Flow<List<Airport>>
}