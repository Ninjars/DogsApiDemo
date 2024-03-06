package net.jeremystevens.dogs.data

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DogsService {
    @GET("breeds/list/all")
    suspend fun getAllBreeds(): Response<NetworkModel.BreedsResponse>

    @GET("breed/{id}/images/random/{count}")
    suspend fun getBreedImages(
        @Path("id") id: String,
        @Path("count") count: Int,
    ): Response<NetworkModel.ImagesResponse>
}

object NetworkModel {
    data class BreedsResponse(
        @SerializedName("message")
        val message: HashMap<String, List<String>>,
    )

    data class ImagesResponse(
        @SerializedName("status")
        val status: String,
        @SerializedName("message")
        val message: List<String>,
    )
}
