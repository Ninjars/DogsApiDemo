package net.jeremystevens.dogs.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.jeremystevens.dogs.data.DogsService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    fun networkClient() =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BASIC
//            })
            .build()

    @Provides
    fun retrofit(client: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun dogsService(retrofit: Retrofit) =
        retrofit.create(DogsService::class.java)
}
