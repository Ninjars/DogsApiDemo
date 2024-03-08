package net.jeremystevens.dogs.features.dogpics

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import net.jeremystevens.dogs.MainCoroutineRule
import net.jeremystevens.dogs.Route
import net.jeremystevens.dogs.data.DataModel
import net.jeremystevens.dogs.data.DataResult
import net.jeremystevens.dogs.data.DogsRepository
import net.jeremystevens.dogs.features.dogpics.DogPicsEvent.TriggerRefresh
import net.jeremystevens.dogs.ui.components.ErrorViewState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class DogPicsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private val repository: DogsRepository = mockk()

    @Test
    fun GIVEN_initialised_THEN_emitsLoadingState() {
        val viewModel = createViewModel()

        val viewState = viewModel.viewState.value

        assertThat(viewState).isEqualTo(DogPicsViewState.Loading)
    }

    @Test
    fun GIVEN_viewModel_WHEN_initialRepositoryFetchCompletesSuccessfully_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(successResult()),
            expectedViewModel = ExpectedSuccessModel
        )
    }

    @Test
    fun GIVEN_viewModel_WHEN_initialRepositoryFetchFails_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(ErrorResult),
            expectedViewModel = ExpectedErrorModel
        )
    }

    @Test
    fun GIVEN_viewModel_WHEN_initialRepositoryFetchReturnsEmpty_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(DataResult.NoData()),
            expectedViewModel = DogPicsViewState.Content(
                photoUrls = emptyList(),
                isRefreshing = false,
                error = ErrorViewState.EmptyResponse,
            )
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchReturnsEmpty_WHEN_refreshTriggered_AND_succeeds_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(DataResult.NoData(), successResult()),
            events = listOf(TriggerRefresh),
            expectedViewModel = ExpectedSuccessModel
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchReturnsEmpty_WHEN_refreshTriggered_AND_fails_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(DataResult.NoData(), ErrorResult),
            events = listOf(TriggerRefresh),
            expectedViewModel = ExpectedErrorModel
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchSuccessful_WHEN_refreshTriggered_AND_fails_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(successResult(), ErrorResult),
            events = listOf(TriggerRefresh),
            expectedViewModel = DogPicsViewState.Content(
                photoUrls = listOf("photoUrl1", "photoUrl2"),
                isRefreshing = false,
                error = ErrorViewState.NetworkError("404"),
            )
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchSuccessful_WHEN_refreshTriggered_AND_newResults_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(successResult(), successResult("test_breed_2")),
            events = listOf(TriggerRefresh),
            expectedViewModel = DogPicsViewState.Content(
                photoUrls = listOf("photoUrl1", "photoUrl2"),
                isRefreshing = false,
                error = null,
            )
        )
    }

    private fun createViewModel(): DogPicsViewModel {
        val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        every { savedStateHandle.get<String>(Route.BreedPhotos.routeBreedId) } returns "test_breed"
        return DogPicsViewModel(
            repository,
            savedStateHandle
        )
    }

    private fun assertViewState(
        repositoryValues: List<DataResult<DataModel.BreedDetails>>,
        events: List<DogPicsEvent> = emptyList(),
        expectedViewModel: DogPicsViewState,
    ) {
        coEvery { repository.getDataForBreed("test_breed", any()) } returnsMany repositoryValues
        val viewModel = createViewModel()

        events.forEach { viewModel.accept(it) }

        val viewState = viewModel.viewState.value

        assertThat(viewState).isEqualTo(expectedViewModel)
    }

    private fun successResult(id: String = "test_breed") =
        DataResult.Success(
            DataModel.BreedDetails(
                id = id,
                images = listOf("photoUrl1", "photoUrl2")
            )
        )

    private companion object {
        val ErrorResult = DataResult.Failure<DataModel.BreedDetails>("404")

        val ExpectedSuccessModel = DogPicsViewState.Content(
            photoUrls = listOf("photoUrl1", "photoUrl2"),
            isRefreshing = false,
            error = null,
        )

        val ExpectedErrorModel = DogPicsViewState.Content(
            photoUrls = emptyList(),
            isRefreshing = false,
            error = ErrorViewState.NetworkError("404"),
        )
    }
}