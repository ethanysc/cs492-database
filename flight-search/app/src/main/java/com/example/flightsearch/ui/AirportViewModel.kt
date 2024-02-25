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

    fun searchAirports(searchString: String): Flow<List<Airport>> = airportDao.searchAirports(searchString)
    fun getFlightsByAirportId(id: Int): Flow<List<Airport>> = airportDao.getFlightsByAirportId(id)

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
)

//package com.example.flightsearch.ui.airport
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.flightsearch.data.Airport
//import com.example.flightsearch.data.airport.AirportsRepository
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//
//class AirportViewModel(private val airportsRepository: AirportsRepository) : ViewModel() {
//    companion object {
//        private const val TIMEOUT_MILLIS = 5_000L
//    }
//
//    var airportUiState = AirportUiState(listOf())
//
//    fun searchAirports(searchString: String) {
//        airportsRepository.searchAirports() //searchString
//            .map { AirportUiState(airportsList = it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = AirportUiState()
//            )
//    }
//
//    fun searchFlightConnections(id: Int) {
//        airportsRepository.getFlightsByAirportId(id)
//            .map { AirportUiState(flightConnectionsList = it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = AirportUiState()
//            )
//    }
//}
//
//data class AirportUiState(
//    val airportsList: List<Airport> = listOf(),
//    val flightConnectionsList: List<Airport> = listOf()
//)