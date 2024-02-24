package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

class OfflineFavoritesRepository(private val favoriteDao: FavoriteDao): FavoritesRepository {
    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()

    override suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    override suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)
}