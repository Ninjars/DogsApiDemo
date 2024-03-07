package net.jeremystevens.dogs.features.breedslist

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import net.jeremystevens.dogs.MainCoroutineRule
import net.jeremystevens.dogs.NavigationDispatcher
import net.jeremystevens.dogs.data.DataModel
import net.jeremystevens.dogs.data.DataResult
import net.jeremystevens.dogs.data.DogsRepository
import net.jeremystevens.dogs.features.breedslist.BreedsListViewState.BreedsListViewContent.DogBreedItem
import net.jeremystevens.dogs.ui.components.ErrorViewState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class BreedsListViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private val repository: DogsRepository = mockk()
    private val navigationDispatcher: NavigationDispatcher = mockk()

    @Test
    fun GIVEN_initialised_THEN_emitsLoadingState() {
        val viewModel = BreedsListViewModel(repository, navigationDispatcher)

        val viewState = viewModel.viewState.value

        assertThat(viewState).isInstanceOf(BreedsListViewState.Loading::class.java)
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
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = emptyList(),
                isRefreshing = false,
                error = ErrorViewState.EmptyResponse,
            )
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchReturnsEmpty_WHEN_refreshTriggered_AND_succeeds_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(DataResult.NoData(), successResult()),
            events = listOf(BreedsListEvent.TriggerRefreshList),
            expectedViewModel = ExpectedSuccessModel
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchReturnsEmpty_WHEN_refreshTriggered_AND_fails_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(DataResult.NoData(), ErrorResult),
            events = listOf(BreedsListEvent.TriggerRefreshList),
            expectedViewModel = ExpectedErrorModel
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchSuccessful_WHEN_refreshTriggered_AND_fails_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(successResult(), ErrorResult),
            events = listOf(BreedsListEvent.TriggerRefreshList),
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = listOf(DogBreedItem(id = "test_breed", displayName = "Test_breed")),
                isRefreshing = false,
                error = ErrorViewState.NetworkError("404"),
            )
        )
    }

    @Test
    fun GIVEN_initialRepositoryFetchSuccessful_WHEN_refreshTriggered_AND_newResults_THEN_emitsUpdatedViewState() {
        assertViewState(
            repositoryValues = listOf(successResult(), successResult("test_breed_2")),
            events = listOf(BreedsListEvent.TriggerRefreshList),
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = listOf(DogBreedItem(id = "test_breed_2", displayName = "Test_breed_2")),
                isRefreshing = false,
                error = null,
            )
        )
    }

    private fun assertViewState(
        repositoryValues: List<DataResult<DataModel.Breeds>>,
        events: List<BreedsListEvent> = emptyList(),
        expectedViewModel: BreedsListViewState,
    ) {
        coEvery { repository.getBreeds() } returnsMany repositoryValues
        val viewModel = BreedsListViewModel(repository, navigationDispatcher)

        events.forEach { viewModel.accept(it) }

        val viewState = viewModel.viewState.value

        assertThat(viewState).isEqualTo(expectedViewModel)
    }

    private fun successResult(id: String = "test_breed") =
        DataResult.Success(DataModel.Breeds(listOf(DataModel.Breeds.BreedData(id))))

    private companion object {
        val ErrorResult = DataResult.Failure<DataModel.Breeds>("404")

        val ExpectedSuccessModel = BreedsListViewState.BreedsListViewContent(
            dogBreeds = listOf(DogBreedItem(id = "test_breed", displayName = "Test_breed")),
            isRefreshing = false,
            error = null,
        )

        val ExpectedErrorModel = BreedsListViewState.BreedsListViewContent(
            dogBreeds = emptyList(),
            isRefreshing = false,
            error = ErrorViewState.NetworkError("404"),
        )
    }
}
