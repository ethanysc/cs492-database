package com.example.flightsearch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp (
    airportViewModel: AirportViewModel = viewModel(factory = AirportViewModel.factory),
    favoriteViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModel.factory),
    modifier: Modifier = Modifier
) {
    val topAppBarTitle = stringResource(R.string.top_bar_title)

    val airportUiState = airportViewModel.airportUiState.collectAsState()
    val searchString = airportUiState.value.searchString
    val isSelectingDestination = airportUiState.value.isSelectingDestination
    val selectedDepartureAirport = airportUiState.value.selectedDepartureAirport

    val airportsList by airportViewModel.searchAirports(searchString).collectAsState(emptyList())
    val suggestionsList by airportViewModel.getFlightsByAirportId(selectedDepartureAirport?.id).collectAsState(emptyList())
    val favoritesList by favoriteViewModel.getAllFavorites().collectAsState(emptyList())

    Scaffold(
        topBar = { FlightSearchTopAppBar(title = topAppBarTitle) }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            FlightSearchInput(
                searchString = searchString,
                airportViewModel = airportViewModel,
                isSelectingDestination = isSelectingDestination
            )
            if (searchString != "" && !isSelectingDestination) { // Render autocomplete suggestions
                FlightSearchAutoComplete(
                    airportsList = airportsList,
                    viewModel = airportViewModel
                )
            } else if (isSelectingDestination) { // Render flight suggestions
                FlightSuggestions(
                    selectedDepartureAirport = selectedDepartureAirport,
                    suggestionsList = suggestionsList,
                    favoriteViewModel = favoriteViewModel
                )
            } else { // Render Favorites
//                FavoriteFlights(
//                    favoritesList = favoritesList,
//                    airportViewModel = airportViewModel,
//                    favoriteViewModel = favoriteViewModel
//                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchTopAppBar (title: String) {
    TopAppBar(
        title = { Text(title, color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchInput (
    searchString: String = "",
    airportViewModel: AirportViewModel,
    isSelectingDestination: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val placeholder = stringResource(R.string.flight_search_placeholder_label)
    TextField(
        value = searchString,
        onValueChange = {
            coroutineScope.launch {
                if (isSelectingDestination) { // If user edits search input while picking flight connection, restart user flow from beginning
                    airportViewModel.toggleIsSelectingDestination()
                    airportViewModel.selectDepartureAirport(null)
                }
                airportViewModel.updateSearchString(it)
            }
        },
        placeholder = { Text(placeholder) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        shape = RoundedCornerShape(75),
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        singleLine = true
    )
}

@Composable
fun FlightSearchAutoComplete(
    airportsList: List<Airport> = emptyList(),
    viewModel: AirportViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        items(airportsList) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable() {
                        coroutineScope.launch {
                            viewModel.selectDepartureAirport(airport)
                            viewModel.toggleIsSelectingDestination()
                        }
                    }
                    .padding(4.dp),
            ) {
                Text(
                    text = airport.iata_code,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = airport.name,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
fun FlightSuggestions(
    selectedDepartureAirport: Airport?,
    suggestionsList: List<Airport>,
    favoriteViewModel: FavoriteViewModel,
    modifier: Modifier = Modifier
) {
    val departureIata = selectedDepartureAirport?.iata_code
    Text(
        text = "Flights from $departureIata",
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        ),
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
    )
    LazyColumn(
        modifier = Modifier.padding(horizontal = 18.dp),
    ) {
        items(suggestionsList) { suggestion ->
            if (selectedDepartureAirport != null) {
                FlightCard(
                    departureAirport = selectedDepartureAirport,
                    arrivalAirport = suggestion,
                    favoriteViewModel = favoriteViewModel
                )
            }
        }
    }
}

//@Composable
//fun FavoriteFlights (
//    favoritesList: List<Favorite>,
//    airportViewModel: AirportViewModel,
//    favoriteViewModel: FavoriteViewModel
//) {
//    if (favoritesList.isEmpty()) {
//        Text(text = "No favorites selected yet")
//    } else {
//        LazyColumn(
//            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
//        ) {
//            items(favoritesList) { favorite ->
//                val departureAirport = airportViewModel.getAirportByIata(favorite.departure_code).collectAsState(EmptyList())
//                val arrivalAirport = airportViewModel.getAirportByIata(favorite.destination_code)
//                FlightCard(
//                    departureAirport = departureAirport,
//                    arrivalAirport = arrivalAirport,
//                    airportViewModel = airportViewModel,
//                    favoriteViewModel = favoriteViewModel
//                )
//            }
//        }
//    }
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightCard(
    departureAirport: Airport,
    arrivalAirport: Airport,
    favoriteViewModel: FavoriteViewModel
) {
    val flightCardDepartureLabel = stringResource(R.string.flight_card_departure_label)
    val flightCardArrivalLabel = stringResource(R.string.flight_card_arrival_label)
    val coroutineScope = rememberCoroutineScope()

    val favoriteFlight = favoriteViewModel
        .getFavoriteByIata(departureAirport.iata_code, arrivalAirport.iata_code)
        .collectAsState(emptyList())
        .value
    val notFavoriteFlight = favoriteFlight.isEmpty()
    val starTintColor = if (notFavoriteFlight) {
            Color(0xFF74777E)
        } else {
            Color(0xFF934B01)
        }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(
            topStartPercent = 0,
            topEndPercent = 12,
            bottomStartPercent = 0,
            bottomEndPercent = 0
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = flightCardDepartureLabel,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)) {
                    Text(
                        text = departureAirport.iata_code,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = departureAirport.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                    )
                }
                Text(
                    text = flightCardArrivalLabel,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = arrivalAirport.iata_code,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = arrivalAirport.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                    )
                }
            }
            Column(modifier = Modifier.offset(30.dp, 30.dp)) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = starTintColor,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            if (notFavoriteFlight) {
                                val newFavorite = Favorite(
                                    departure_code = departureAirport.iata_code,
                                    destination_code = arrivalAirport.iata_code
                                )
                                coroutineScope.launch { favoriteViewModel.insert(newFavorite) }
                            } else {
                                coroutineScope.launch { favoriteViewModel.delete(favoriteFlight.first()) }
                            }
                        }
                )
            }
        }
    }
}