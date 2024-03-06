package net.jeremystevens.dogs.features.dogpics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.jeremystevens.dogs.Route
import net.jeremystevens.dogs.data.DataResult
import net.jeremystevens.dogs.data.DogsRepository
import net.jeremystevens.dogs.features.dogpics.DogPicsViewModel.State
import net.jeremystevens.dogs.ui.components.ErrorViewState
import net.jeremystevens.dogs.utils.toViewState
import java.util.function.Consumer
import javax.inject.Inject


@HiltViewModel
class DogPicsViewModel @Inject constructor(
    private val repository: DogsRepository,
    savedStateHandle: SavedStateHandle,
) : Consumer<DogPicsEvent>, ViewModel() {
    private val breedId = savedStateHandle.get<String>(Route.BreedPhotos.routeBreedId)!!
    private val stateFlow = MutableStateFlow<State>(State.Loading)

    val viewState: StateFlow<DogPicsViewState> =
        stateFlow.toViewState(viewModelScope) { it.toViewState() }

    init {
        viewModelScope.launch {
            stateFlow.value = updateState(breedId, stateFlow.value, repository)
        }
    }

    override fun accept(event: DogPicsEvent) {
        viewModelScope.launch {
            when (event) {
                is DogPicsEvent.TriggerRefresh -> {
                    stateFlow.value = stateFlow.value.setIsRefreshing()
                    stateFlow.value = updateState(breedId, stateFlow.value, repository)
                }
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class Content(
            val photoUrls: List<String>,
            val isRefreshing: Boolean,
            val error: ErrorState?,
        ) : State {
            data class ErrorState(
                val code: String?,
            )
            companion object {
                val Default = Content(emptyList(), false, null)
            }
        }
    }
}

private fun State.setIsRefreshing(): State =
    when (this) {
        is State.Content -> this.copy(isRefreshing = true, error = null)
        is State.Loading -> this
    }

private suspend fun updateState(breedId: String, state: State, repository: DogsRepository): State {
    val data = repository.getDataForBreed(breedId)
    val loadedState = when (state) {
        is State.Content -> state
        is State.Loading -> State.Content.Default
    }
    return when (data) {
        is DataResult.Failure -> loadedState.copy(
            isRefreshing = false,
            error = State.Content.ErrorState(data.error),
        )
        is DataResult.NoData -> loadedState.copy(
            isRefreshing = false,
            error = State.Content.ErrorState(null),
        )
        is DataResult.Success -> loadedState.copy(
            photoUrls = data.data.images,
            isRefreshing = false,
            error = null,
        )
    }
}

private fun State.toViewState(): DogPicsViewState =
    when (this) {
        is State.Content -> DogPicsViewState.Content(
            photoUrls = photoUrls,
            isRefreshing = isRefreshing,
            error = error?.toErrorViewState(),
        )

        is State.Loading -> DogPicsViewState.Loading
    }

private fun State.Content.ErrorState.toErrorViewState(): ErrorViewState =
    if (code == null) {
        ErrorViewState.EmptyResponse
    } else {
        ErrorViewState.NetworkError(code)
    }
