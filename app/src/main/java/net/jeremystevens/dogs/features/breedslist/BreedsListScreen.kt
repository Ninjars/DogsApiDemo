package net.jeremystevens.dogs.features.breedslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        onRefreshTriggered = { eventHandler(BreedsListEvent.TriggerRefreshList) }
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
        onClick = { eventHandler(BreedsListEvent.BreedSelected(state.id)) },
        modifier = modifier
    ) {
        Text(
            text = state.displayName,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenPreview() {
    DogsTheme {
        BreedsListContent(
            state = BreedsListViewContent(
                dogBreeds = listOf(
                    BreedsListViewContent.DogBreedItem("1", "Breed 1"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3"),
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
                    BreedsListViewContent.DogBreedItem("1", "Breed 1"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3"),
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
                    BreedsListViewContent.DogBreedItem("1", "Breed 1"),
                    BreedsListViewContent.DogBreedItem("2", "Breed 2"),
                    BreedsListViewContent.DogBreedItem("3", "Breed 3"),
                ),
                isRefreshing = true,
                error = ErrorViewState.NetworkError(404),
            ),
        ) {}
    }
}
