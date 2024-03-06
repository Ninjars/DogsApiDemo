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
import org.assertj.core.api.InstanceOfAssertFactories
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
        assertInitialLoadResult(
            repositoryValue = DataResult.Success(
                DataModel.Breeds(
                    listOf(DataModel.Breeds.BreedData("test_breed"))
                )
            ),
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = listOf(DogBreedItem(id = "test_breed", displayName = "Test_breed")),
                isRefreshing = false,
                error = null,
            )
        )
    }

    @Test
    fun GIVEN_viewModel_WHEN_initialRepositoryFetchFails_THEN_emitsUpdatedViewState() {
        assertInitialLoadResult(
            repositoryValue = DataResult.Failure("404"),
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = emptyList(),
                isRefreshing = false,
                error = ErrorViewState.NetworkError("404"),
            )
        )
    }

    @Test
    fun GIVEN_viewModel_WHEN_initialRepositoryFetchReturnsEmpty_THEN_emitsUpdatedViewState() {
        assertInitialLoadResult(
            repositoryValue = DataResult.NoData(),
            expectedViewModel = BreedsListViewState.BreedsListViewContent(
                dogBreeds = emptyList(),
                isRefreshing = false,
                error = ErrorViewState.EmptyResponse,
            )
        )
    }

    private fun assertInitialLoadResult(
        repositoryValue: DataResult<DataModel.Breeds>,
        expectedViewModel: BreedsListViewState,
    ) {
        coEvery { repository.getBreeds() } returns repositoryValue
        val viewModel = BreedsListViewModel(repository, navigationDispatcher)

        val viewState = viewModel.viewState.value

        assertThat(viewState)
            .asInstanceOf(InstanceOfAssertFactories.type(BreedsListViewState.BreedsListViewContent::class.java))
            .isEqualTo(expectedViewModel)
    }
}
