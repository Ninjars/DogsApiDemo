package net.jeremystevens.dogs.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import net.jeremystevens.dogs.ui.components.Refreshable
import net.jeremystevens.dogs.ui.screens.BreedPhotosState.BreedPhotosContent
import net.jeremystevens.dogs.ui.screens.BreedPhotosState.Loading
import net.jeremystevens.dogs.ui.theme.DogsTheme

sealed class BreedPhotosState {
    data object Loading : BreedPhotosState()
    data class BreedPhotosContent(
        val photoUrls: List<String>,
        val isRefreshing: Boolean,
    ) : BreedPhotosState()
}

sealed class BreedPhotoEvent {
    data object TriggerRefresh : BreedPhotoEvent()
}

private const val MinImageSizeDp = 100

@Composable
fun BreedPhotosScreen() {

}

@Composable
private fun BreedPhotoStateSwitcher(
    state: BreedPhotosState,
    eventHandler: (BreedPhotoEvent) -> Unit,
) {
    when (state) {
        is BreedPhotosContent -> BreedPhotosContent(state, eventHandler)
        is Loading -> LoadingScreen()
    }
}

@Composable
private fun BreedPhotosContent(
    state: BreedPhotosContent,
    eventHandler: (BreedPhotoEvent) -> Unit,
) {
    Refreshable(
        isRefreshing = state.isRefreshing,
        onRefreshTriggered = { eventHandler(BreedPhotoEvent.TriggerRefresh) },
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(MinImageSizeDp.dp),
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = state.photoUrls, key = { it }) {
                BreedPhotoItem(it)
            }
        }
    }
}

@Composable
private fun BreedPhotoItem(
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
private fun DogPhotosScreenPreview() {
    DogsTheme {
        BreedPhotoStateSwitcher(
            state = BreedPhotosContent(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = false,
            ),
            eventHandler = {},
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogPhotosScreenRefreshingPreview() {
    DogsTheme {
        BreedPhotoStateSwitcher(
            state = BreedPhotosContent(
                photoUrls = listOf("1", "2", "3"),
                isRefreshing = true,
            ),
            eventHandler = {},
        )
    }
}
