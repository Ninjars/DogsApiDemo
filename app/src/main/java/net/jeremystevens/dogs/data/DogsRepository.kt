package net.jeremystevens.dogs.data

interface DogsRepository {
    suspend fun getBreeds(): DataResult<DataModel.Breeds>
    suspend fun getDataForBreed(breedId: String): DataResult<DataModel.BreedDetails>
}

/**
 * This repository allows for expansion to support a local as well as remote data source.
 * In this network-only implementation it is a thin layer, implemented mostly to adhere to the
 * architectural pattern.
 */
class DogsRepositoryImpl(
    private val remoteDataSource: DogsRemoteDataSource,
) : DogsRepository {
    override suspend fun getBreeds() =
        remoteDataSource.getBreeds()

    override suspend fun getDataForBreed(breedId: String) =
        remoteDataSource.getDataForBreed(breedId)
}

class StubDogsRepository : DogsRepository {
    override suspend fun getBreeds(): DataResult<DataModel.Breeds> =
        DataResult.Success(DataModel.Breeds(emptyList()))

    override suspend fun getDataForBreed(breedId: String): DataResult<DataModel.BreedDetails> =
        DataResult.Success(DataModel.BreedDetails(emptyList()))
}
