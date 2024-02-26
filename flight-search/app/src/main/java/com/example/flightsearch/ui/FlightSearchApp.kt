import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.AirportViewModel
import com.example.flightsearch.ui.FavoriteViewModel
import com.example.flightsearch.ui.theme.FlightSearchTheme
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
    val suggestionDepartureId = airportUiState.value.suggestionDepartureId

    val airportsList by airportViewModel.searchAirports(searchString).collectAsState(emptyList())
    val suggestionsList by airportViewModel.getFlightsByAirportId(suggestionDepartureId).collectAsState(emptyList())
    val favoritesList by favoriteViewModel.getAllFavorites().collectAsState(emptyList())

    Scaffold(
        topBar = { FlightSearchTopAppBar(title = topAppBarTitle) }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0x1F5FA6))
        ) {
            FlightSearchInput(
                searchString = searchString,
                viewModel = airportViewModel,
                isSelectingDestination = isSelectingDestination
            )
            if (searchString != "") { // Render autocomplete suggestions
                FlightSearchAutoComplete(
                    airportsList = airportsList,
                    viewModel = airportViewModel
                )
            } else if (isSelectingDestination) { // Render flight suggestions
                FlightSuggestions(
                    suggestionsList = suggestionsList,
                    favoriteViewModel = favoriteViewModel
                )
            } else { // Render Favorites
                Text(text = "Favorites")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchTopAppBar (title: String) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    )
}

@Composable
fun FlightSearchInput (
    searchString: String = "",
    viewModel: AirportViewModel,
    isSelectingDestination: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val placeholder = stringResource(R.string.flight_search_placeholder_label)
    OutlinedTextField(
        value = searchString,
        onValueChange = {
            coroutineScope.launch {
                if (isSelectingDestination) { // If user edits search input while picking flight connection, restart user flow from beginning
                    viewModel.toggleIsSelectingDestination()
                    viewModel.selectSuggestionDepartureId(null)
                }
                viewModel.updateSearchString(it)
            }
        },
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                            viewModel.getFlightsByAirportId(airport.id)
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
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
fun FlightSuggestions(
    suggestionsList: List<Airport>,
    favoriteViewModel: FavoriteViewModel,
    modifier: Modifier = Modifier
) {

}

@Preview(showBackground = true)
@Composable
private fun FlightSearchApp() {
    FlightSearchTheme {
        FlightSearchApp()
    }
}