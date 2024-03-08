package net.jeremystevens.dogs.features.breedslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.jeremystevens.dogs.features.breedslist.BreedsListViewState.BreedsListViewContent
import net.jeremystevens.dogs.ui.components.ErrorPopup
import net.jeremystevens.dogs.ui.components.ErrorViewState
import net.jeremystevens.dogs.ui.components.LoadingScreen
import net.jeremystevens.dogs.ui.components.Refreshable
import net.jeremystevens.dogs.ui.theme.DogsTheme
import net.jeremystevens.dogs.utils.rememberEventConsumer

@Stable
sealed class BreedsListViewState {
    data object Loading : BreedsListViewState()
    data class BreedsListViewContent(
        val dogBreeds: List<DogBreedItem>,
        val isRefreshing: Boolean,
        val error: ErrorViewState?,
    ) : BreedsListViewState() {
        data class DogBreedItem(
            val id: String,
            val displayName: String,
            val photoUrl: String?,
        )
    }
}

sealed class BreedsListEvent {
    data class BreedSelected(val id: String) : BreedsListEvent()
    data object TriggerRefreshList : BreedsListEvent()
}

@Composable
fun BreedsListScreen(
    viewModel: BreedsListViewModel,
) {
    BreedsListStateSwitcher(viewModel.viewState.collectAsState(), rememberEventConsumer(viewModel))
}

@Composable
private fun BreedsListStateSwitcher(
    state: State<BreedsListViewState>,
    eventHandler: (BreedsListEvent) -> Unit,
) {
    when (val stateValue = state.value) {
        is BreedsListViewContent -> BreedsListContent(stateValue, eventHandler)
        is BreedsListViewState.Loading -> LoadingScreen()
    }
}

@Composable
private fun BreedsListContent(
    state: BreedsListViewContent,
    eventHandler: (BreedsListEvent) -> Unit,
) {
    Refreshable(
        isRefreshing = state.isRefreshing,
        onRefreshTriggered = remember { { eventHandler(BreedsListEvent.TriggerRefreshList) }}
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.dogBreeds, key = { it.id }) {
                    DogBreedView(
                        state = it,
                        eventHandler = eventHandler,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            ErrorPopup(state.error)
        }
    }
}

@Composable
private fun DogBreedView(
    state: BreedsListViewContent.DogBreedItem,
    eventHandler: (BreedsListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = remember(state.id) { { eventHandler(BreedsListEvent.BreedSelected(state.id)) } },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            AsyncImage(
                model = state.photoUrl,
                placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
            Text(
                text = state.displayName,
            )
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenPreview() {
    DogsTheme {
        BreedsListContent(
            state = BreedsListViewContent(
                dogBreeds = listOf(
                    BreedsListViewContent.DogBreedItem("1", "Breed 1", "url"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2", "url"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3", "url"),
                ),
                isRefreshing = false,
                error = null,
            )
        ) {}
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenRefreshingPreview() {
    DogsTheme {
        BreedsListContent(
            state = BreedsListViewContent(
                dogBreeds = listOf(
                    BreedsListViewContent.DogBreedItem("1", "Breed 1", "url"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2", "url"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3", "url"),
                ),
                isRefreshing = true,
                error = null,
            ),
        ) {}
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenErrorPreview() {
    DogsTheme {
        BreedsListContent(
            state = BreedsListViewContent(
                dogBreeds = listOf(
                    BreedsListViewContent.DogBreedItem("1", "Breed 1", "url"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2", "url"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3", null),
                ),
                isRefreshing = true,
                error = ErrorViewState.NetworkError("404"),
            ),
        ) {}
    }
}
