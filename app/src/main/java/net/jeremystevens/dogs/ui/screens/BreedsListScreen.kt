package net.jeremystevens.dogs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    ) : BreedsListState() {
        data class DogBreedItem(
            val id: String,
            val displayName: String,
        )
    }
}

sealed class BreedsListEvent {
    data class BreedSelected(val id: String) : BreedsListEvent()
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

@Composable
private fun BreedsListContent(
    state: BreedsListContent,
    eventHandler: (BreedsListEvent) -> Unit,
) {
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
                )
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
