package net.jeremystevens.dogs.features.breedslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.jeremystevens.dogs.NavigationDispatcher
import net.jeremystevens.dogs.Route
import net.jeremystevens.dogs.data.DataResult
import net.jeremystevens.dogs.data.DogsRepository
import net.jeremystevens.dogs.features.breedslist.BreedsListViewModel.State
import net.jeremystevens.dogs.features.breedslist.BreedsListViewModel.State.BreedsList
import net.jeremystevens.dogs.features.breedslist.BreedsListViewModel.State.Loading
import net.jeremystevens.dogs.features.breedslist.BreedsListViewState.BreedsListViewContent
import net.jeremystevens.dogs.ui.components.ErrorViewState
import net.jeremystevens.dogs.utils.toViewState
import java.util.function.Consumer
import javax.inject.Inject

@HiltViewModel
class BreedsListViewModel @Inject constructor(
    private val repository: DogsRepository,
    private val navigationDispatcher: NavigationDispatcher,
) : Consumer<BreedsListEvent>, ViewModel() {
    private val stateFlow = MutableStateFlow<State>(Loading)

    val viewState: StateFlow<BreedsListViewState> =
        stateFlow.toViewState(viewModelScope) { it.toViewState() }

    init {
        viewModelScope.launch {
            stateFlow.value = updateState(stateFlow.value, repository)
        }
    }

    override fun accept(event: BreedsListEvent) {
        viewModelScope.launch {
            when (event) {
                is BreedsListEvent.BreedSelected -> navigationDispatcher.navigateTo(
                    Route.BreedPhotos(
                        event.id
                    )
                )

                is BreedsListEvent.TriggerRefreshList -> {
                    stateFlow.value = stateFlow.value.setIsRefreshing()
                    stateFlow.value = updateState(stateFlow.value, repository)
                }
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class BreedsList(
            val dogBreeds: List<DogBreedItem>,
            val isRefreshing: Boolean,
            val error: ErrorState?,
        ) : State {
            data class DogBreedItem(
                val id: String,
                val photoUrl: String?,
            )

            data class ErrorState(
                val code: String?,
            )

            companion object {
                val Default = BreedsList(emptyList(), false, null)
            }
        }
    }
}

private fun State.setIsRefreshing(): State =
    when (this) {
        is BreedsList -> this.copy(isRefreshing = true, error = null)
        is Loading -> this
    }

private fun State.toViewState(): BreedsListViewState =
    when (this) {
        is BreedsList -> BreedsListViewContent(
            isRefreshing = isRefreshing,
            dogBreeds = dogBreeds.map {
                BreedsListViewContent.DogBreedItem(
                    id = it.id,
                    displayName = it.id.replaceFirstChar { char -> char.uppercase() },
                    photoUrl = it.photoUrl,
                )
            },
            error = error?.toErrorViewState(),
        )

        is Loading -> BreedsListViewState.Loading
    }

private fun BreedsList.ErrorState.toErrorViewState(): ErrorViewState =
    if (code == null) {
        ErrorViewState.EmptyResponse
    } else {
        ErrorViewState.NetworkError(code)
    }

private suspend fun updateState(state: State, repository: DogsRepository): State {
    val data = repository.getBreeds()

    // start from a blank slate if coming from Loading state,
    // but keep previous breeds list for error cases otherwise
    // to soften the UX of refreshing with no network
    val loadedState = when (state) {
        is BreedsList -> state
        is Loading -> BreedsList.Default
    }
    return when (data) {
        is DataResult.Failure -> loadedState.copy(
            isRefreshing = false,
            error = BreedsList.ErrorState(data.error),
        )

        is DataResult.NoData -> loadedState.copy(
            isRefreshing = false,
            error = BreedsList.ErrorState(null),
        )

        is DataResult.Success -> loadedState.copy(
            dogBreeds = data.data.breeds.map { BreedsList.DogBreedItem(it.id, it.photoUrl) },
            isRefreshing = false,
            error = null,
        )
    }
}
