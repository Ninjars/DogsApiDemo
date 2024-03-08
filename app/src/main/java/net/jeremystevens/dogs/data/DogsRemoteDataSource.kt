package net.jeremystevens.dogs.data

import net.jeremystevens.dogs.data.DataModel.BreedDetails
import net.jeremystevens.dogs.data.DataModel.Breeds
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DogsRemoteDataSource @Inject constructor(
    private val service: DogsService,
) {
    suspend fun getBreeds(): DataResult<Breeds> =
        try {
            service.getAllBreeds().extractResult { it.mapToDataModel() }
        } catch (e: Exception) {
            DataResult.Failure("Exception: ${e.message}")
        }

    suspend fun getDataForBreed(breedId: String, count: Int): DataResult<BreedDetails> =
        try {
            service.getBreedImages(id = breedId, count = count).extractResult { it.mapToDataModel() }
        } catch (e: Exception) {
            DataResult.Failure("Exception: ${e.message}")
        }

    private fun <T, S> Response<T>.extractResult(mapper: (T) -> S): DataResult<S> {
        val body = body()
        return when {
            !isSuccessful ->
                DataResult.Failure(code().toString())

            body == null ->
                DataResult.NoData()

            isSuccessful ->
                DataResult.Success(mapper(body))

            else ->
                throw IllegalStateException("unexpected network result case")
        }
    }

    private fun NetworkModel.BreedsResponse.mapToDataModel() =
        Breeds(
            message.keys.map { Breeds.BreedData(it) }
        )

    private fun NetworkModel.ImagesResponse.mapToDataModel() =
        BreedDetails(
            message
        )
}
