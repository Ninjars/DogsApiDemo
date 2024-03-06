package net.jeremystevens.dogs.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.jeremystevens.dogs.data.DogsRemoteDataSource
import net.jeremystevens.dogs.data.DogsRepository
import net.jeremystevens.dogs.data.DogsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun repository(remoteDataSource: DogsRemoteDataSource): DogsRepository =
        DogsRepositoryImpl(remoteDataSource)
}
