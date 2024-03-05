package net.jeremystevens.dogs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.jeremystevens.dogs.R
import net.jeremystevens.dogs.ui.screens.BreedsListState.BreedsListContent
import net.jeremystevens.dogs.ui.theme.DogsTheme

sealed class BreedsListState {
    data object Loading : BreedsListState()
    data class BreedsListContent(
        val dogBreeds: List<DogBreedItem>,
        val isRefreshing: Boolean,
    ) : BreedsListState() {
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
) {
}

@Composable
fun BreedsListStateSwitcher(
    state: BreedsListState,
    eventHandler: (BreedsListEvent) -> Unit,
) {
    when (state) {
        is BreedsListContent -> BreedsListContent(state, eventHandler)
        is BreedsListState.Loading -> LoadingState()
    }
}

@Composable
private fun LoadingState() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.breeds_loading_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BreedsListContent(
    state: BreedsListContent,
    eventHandler: (BreedsListEvent) -> Unit,
) {
    /**
     * The current PullToRefresh library offered by Material3 doesn't provide a callback
     * for when the refresh is started and doesn't offer a good way to hoist the internal
     * refresh indicator state to business logic.
     *
     * By maintaining our own "isRefreshed" state flag and propagating an event when the
     * PullToRefreshState changes we can work around this limitation without looking for
     * third party dependencies.
     */

    // synchronise PullToRefresh UI with state
    val refreshState = rememberPullToRefreshState()
    if (!state.isRefreshing) {
        refreshState.endRefresh()
    } else if (!refreshState.isRefreshing) {
        refreshState.startRefresh()
    }

    // detect UI-initiated refresh
    if (refreshState.isRefreshing && !state.isRefreshing) {
        eventHandler(BreedsListEvent.TriggerRefreshList)
    }

    Box(
        Modifier.nestedScroll(refreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (!refreshState.isRefreshing) {
                items(items = state.dogBreeds, key = { it.id }) {
                    DogBreedView(
                        state = it,
                        eventHandler = eventHandler,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = refreshState,
        )
    }
}

@Composable
private fun DogBreedView(
    state: BreedsListContent.DogBreedItem,
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
        BreedsListStateSwitcher(
            state = BreedsListContent(
                dogBreeds = listOf(
                    BreedsListContent.DogBreedItem("1", "Breed 1"),
                    BreedsListContent.DogBreedItem("2", "Breed 2"),
                    BreedsListContent.DogBreedItem("3", "Breed 3"),
                ),
                isRefreshing = false,
            ),
            eventHandler = {},
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenRefreshingPreview() {
    DogsTheme {
        BreedsListStateSwitcher(
            state = BreedsListContent(
                dogBreeds = listOf(
                    BreedsListContent.DogBreedItem("1", "Breed 1"),
                    BreedsListContent.DogBreedItem("2", "Breed 2"),
                    BreedsListContent.DogBreedItem("3", "Breed 3"),
                ),
                isRefreshing = true,
            ),
            eventHandler = {},
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun DogBreedScreenLoadingPreview() {
    DogsTheme {
        BreedsListStateSwitcher(
            state = BreedsListState.Loading,
            eventHandler = {},
        )
    }
}
