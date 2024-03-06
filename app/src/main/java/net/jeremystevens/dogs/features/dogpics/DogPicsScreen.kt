package net.jeremystevens.dogs.features.dogpics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.jeremystevens.dogs.features.dogpics.DogPicsViewState.Content
import net.jeremystevens.dogs.features.dogpics.DogPicsViewState.Loading
import net.jeremystevens.dogs.ui.components.ErrorPopup
import net.jeremystevens.dogs.ui.components.ErrorViewState
import net.jeremystevens.dogs.ui.components.LoadingScreen
import net.jeremystevens.dogs.ui.components.Refreshable
import net.jeremystevens.dogs.ui.theme.DogsTheme
import net.jeremystevens.dogs.utils.rememberEventConsumer

sealed class DogPicsViewState {
    data object Loading : DogPicsViewState()
    data class Content(
        val photoUrls: List<String>,
        val isRefreshing: Boolean,
        val error: ErrorViewState?,
    ) : DogPicsViewState()
}

sealed class DogPicsEvent {
    data object TriggerRefresh : DogPicsEvent()
}

@Composable
fun DogPicsScreen(
    viewModel: DogPicsViewModel,
) {
    DogPicsStateSwitcher(viewModel.viewState.collectAsState(), rememberEventConsumer(viewModel))
}

@Composable
private fun DogPicsStateSwitcher(
    state: State<DogPicsViewState>,
    eventHandler: (DogPicsEvent) -> Unit,
) {
    when (val stateValue = state.value) {
        is Content -> DogPicsContent(stateValue, eventHandler)
        is Loading -> LoadingScreen()
    }
}

@Composable
private fun DogPicsContent(
    state: Content,
    eventHandler: (DogPicsEvent) -> Unit,
) {
    Refreshable(
        isRefreshing = state.isRefreshing,
        onRefreshTriggered = { eventHandler(DogPicsEvent.TriggerRefresh) },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.photoUrls, key = { it }) {
                    DogPicsItem(it)
                }
            }
            ErrorPopup(state.error)
        }
    }
}

@Composable
private fun DogPicsItem(
    url: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        AsyncImage(
            model = url,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = rememberVectorPainter(Icons.Default.Warning),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogPicsScreenPreview() {
    DogsTheme {
        DogPicsContent(
            state = Content(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = false,
                error = null,
            ),
            eventHandler = {},
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogPhotosScreenRefreshingPreview() {
    DogsTheme {
        DogPicsContent(
            state = Content(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = true,
                error = null,
            ),
            eventHandler = {},
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogPicsScreenErrorPreview() {
    DogsTheme {
        DogPicsContent(
            state = Content(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = false,
                error = ErrorViewState.NetworkError("404"),
            ),
            eventHandler = {},
        )
    }
}
