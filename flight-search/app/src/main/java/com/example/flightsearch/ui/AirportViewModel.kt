package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AirportViewModel(private val airportDao: AirportDao): ViewModel() {

    private val _uiState = MutableStateFlow(AirportUiState())
    val airportUiState: StateFlow<AirportUiState> = _uiState.asStateFlow()

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        _uiState.value = AirportUiState()
    }

    fun updateSearchString(searchString: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchString = searchString
            )
        }
    }
    fun toggleIsSelectingDestination() {
        _uiState.update { currentState ->
            currentState.copy(
                isSelectingDestination = currentState.isSelectingDestination == false
            )
        }
    }

    fun selectDepartureAirport(selectedDepartureAirport: Airport?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDepartureAirport = selectedDepartureAirport
            )
        }
    }

    fun searchAirports(searchString: String): Flow<List<Airport>> = airportDao.searchAirports(searchString)
    fun getFlightsByAirportId(id: Int?): Flow<List<Airport>> = airportDao.getFlightsByAirportId(id)
    fun getAirportByIata(iata: String): Flow<List<Airport>> = airportDao.getAirportByIata(iata)

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                AirportViewModel(application.database.airportDao())
            }
        }
    }
}

data class AirportUiState(
    val searchString: String = "",
    val isSelectingDestination: Boolean = false,
    val selectedDepartureAirport: Airport? = null,
)