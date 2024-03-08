package net.jeremystevens.dogs.data

interface DogsRepository {
    suspend fun getBreeds(): DataResult<DataModel.Breeds>
    suspend fun getDataForBreed(breedId: String, count: Int): DataResult<DataModel.BreedDetails>
}

/**
 * This repository allows for expansion to support a local as well as remote data source.
 * In this network-only implementation it is a thin layer, implemented mostly to adhere to the
 * architectural pattern.
 *
 * The interface simplifies replacing the class in tests, though in this project I'm using mockk anyway.
 */
class DogsRepositoryImpl(
    private val remoteDataSource: DogsRemoteDataSource,
) : DogsRepository {
    override suspend fun getBreeds() =
        remoteDataSource.getBreeds()

    override suspend fun getDataForBreed(breedId: String, count: Int) =
        remoteDataSource.getDataForBreed(breedId, count)
}
