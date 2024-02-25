package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query(
        """
        SELECT * from airport 
        WHERE ( name LIKE '%' || :searchString || '%' OR iata_code LIKE '%' || :searchString || '%' )
        ORDER BY passengers DESC
        """
    )
    fun searchAirports(searchString: String): Flow<List<Airport>>


    @Query(
        """
        SELECT * from airport  
        WHERE id != :id 
        ORDER by passengers DESC 
        """
    )
    fun getFlightsByAirportId(id: Int): Flow<List<Airport>>
}