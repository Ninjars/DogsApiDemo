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
        service.getAllBreeds().extractResult { it.mapToDataModel() }

    suspend fun getDataForBreed(breedId: String): DataResult<BreedDetails> =
        service.getBreedImages(id = breedId, count = 10).extractResult { it.mapToDataModel() }

    private fun <T, S> Response<T>.extractResult(mapper: (T) -> S): DataResult<S> {
        val body = body()
        return when {
            !isSuccessful ->
                DataResult.Failure(code())

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
