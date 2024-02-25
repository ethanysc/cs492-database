import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.ui.AirportViewModel
import com.example.flightsearch.ui.FavoriteViewModel


@Composable
fun FlightSearchApp (
    modifier: Modifier = Modifier,
    airportViewModel: AirportViewModel = viewModel(factory = AirportViewModel.factory),
    favoriteViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModel.factory)
) {
    val airportsList by airportViewModel.searchAirports("").collectAsState(emptyList())
    val favoriteList by favoriteViewModel.getAllFavorites().collectAsState(emptyList())

    Text(text = "Hello")
}