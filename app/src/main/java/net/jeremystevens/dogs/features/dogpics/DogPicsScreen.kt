package net.jeremystevens.dogs.features.dogpics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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

private const val MinImageSizeDp = 100

@Composable
fun DogPicsScreen() {

}

@Composable
private fun DogPicsStateSwitcher(
    state: DogPicsViewState,
    eventHandler: (DogPicsEvent) -> Unit,
) {
    when (state) {
        is Content -> DogPicsContent(state, eventHandler)
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
                columns = StaggeredGridCells.Adaptive(MinImageSizeDp.dp),
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
            .wrapContentSize()
    ) {
        AsyncImage(
            model = url,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = rememberVectorPainter(Icons.Default.Warning),
            contentDescription = null,
            modifier = Modifier.defaultMinSize(MinImageSizeDp.dp, MinImageSizeDp.dp)
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogPicsScreenPreview() {
    DogsTheme {
        DogPicsStateSwitcher(
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
        DogPicsStateSwitcher(
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
        DogPicsStateSwitcher(
            state = Content(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = false,
                error = ErrorViewState.NetworkError(404),
            ),
            eventHandler = {},
        )
    }
}
