package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteDao
import kotlinx.coroutines.flow.Flow

class FavoriteViewModel(private val favoriteDao: FavoriteDao): ViewModel() {
    fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()
    suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)
    suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[APPLICATION_KEY] as FlightSearchApplication)
                FavoriteViewModel(application.database.favoriteDao())
            }
        }
    }
}