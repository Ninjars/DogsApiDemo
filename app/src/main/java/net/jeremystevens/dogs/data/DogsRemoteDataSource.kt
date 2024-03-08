package net.jeremystevens.dogs.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
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
            service.getAllBreeds().extractResult {
                val photoUrls = getSingleImagesForBreeds(it.message.keys.toList())
                it.mapToDataModel(photoUrls)
            }
        } catch (e: Exception) {
            DataResult.Failure("Exception: ${e.message}")
        }

    private suspend fun getSingleImagesForBreeds(breedIds: List<String>) =
        withContext(Dispatchers.IO) {
            return@withContext breedIds.map {
                async { getDataForBreed(it, 1) }
            }.awaitAll()
        }

    suspend fun getDataForBreed(breedId: String, count: Int): DataResult<BreedDetails> =
        try {
            service.getBreedImages(id = breedId, count = count)
                .extractResult { it.mapToDataModel(breedId) }
        } catch (e: Exception) {
            DataResult.Failure("Exception: ${e.message}")
        }

    private suspend fun <T, S> Response<T>.extractResult(mapper: suspend (T) -> S): DataResult<S> {
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

    private fun NetworkModel.BreedsResponse.mapToDataModel(photoUrls: List<DataResult<BreedDetails>>) =
        Breeds(
            message.keys.map {
                Breeds.BreedData(it, getPhoto(it, photoUrls))
            }
        )

    private fun getPhoto(id: String, photoUrls: List<DataResult<BreedDetails>>): String? =
        photoUrls.mapNotNull { result ->
            when (result) {
                is DataResult.Failure,
                is DataResult.NoData -> null

                is DataResult.Success -> result.data.images.firstOrNull()
                    .takeIf { result.data.id == id }
            }
        }.firstOrNull()

    private fun NetworkModel.ImagesResponse.mapToDataModel(breedId: String) =
        BreedDetails(
            id = breedId,
            images = message,
        )
}
