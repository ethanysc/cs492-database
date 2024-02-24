package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Favorite>>

    suspend fun insert(favorite: Favorite)

    suspend fun delete(favorite: Favorite)
}