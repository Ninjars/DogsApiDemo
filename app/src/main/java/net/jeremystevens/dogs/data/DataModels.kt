package net.jeremystevens.dogs.data

object DataModel {
    data class Breeds(val breeds: List<BreedData>) {
        data class BreedData(val id: String)
    }
    data class BreedDetails(val images: List<String>)
}

sealed class DataResult<T>(val isSuccess: Boolean) {
    data class Success<T>(val data: T) : DataResult<T>(isSuccess = true)
    class NoData<T> : DataResult<T>(isSuccess = false)
    data class Failure<T>(val errorCode: Int) : DataResult<T>(isSuccess = false)
}
