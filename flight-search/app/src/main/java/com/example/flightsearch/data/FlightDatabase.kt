package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.airport.Airport
import com.example.flightsearch.data.airport.AirportDao
import com.example.flightsearch.data.favorite.Favorite
import com.example.flightsearch.data.favorite.FavoriteDao

@Database(
    entities = [Airport::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class FlightDatabase: RoomDatabase() {

    abstract fun airportsDao(): AirportDao
    abstract fun favoritesDao(): FavoriteDao

    companion object {
        @Volatile
        private var Instance: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlightDatabase::class.java, "flight_database")
                    .build().also { Instance = it }
            }
        }
    }
}