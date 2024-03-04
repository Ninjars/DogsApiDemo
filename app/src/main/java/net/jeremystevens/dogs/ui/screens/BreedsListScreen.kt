package net.jeremystevens.dogs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.jeremystevens.dogs.ui.theme.DogsTheme

data class BreedsListState(
    val dogBreeds: List<DogBreedItem>,
) {
    data class DogBreedItem(
        val id: String,
        val displayName: String,
    )
}

sealed class BreedsListEvent {
    data class BreedSelected(val id: String) : BreedsListEvent()
}

@Composable
fun BreedsListScreen() {

}

@Composable
private fun BreedsListContent(
    state: BreedsListState,
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
    state: BreedsListState.DogBreedItem,
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
            state = BreedsListState(
                dogBreeds = listOf(
                    BreedsListState.DogBreedItem("1", "Breed 1"),
                    BreedsListState.DogBreedItem("2", "Breed 2"),
                    BreedsListState.DogBreedItem("3", "Breed 3"),
                )
            ),
            eventHandler = {},
        )
    }
}
