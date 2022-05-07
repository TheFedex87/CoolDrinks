package it.thefedex87.networkresponsestateeventtest.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.thefedex87.networkresponsestateeventtest.data.remote.TheCocktailDbApi
import it.thefedex87.networkresponsestateeventtest.data.repository.CocktailRepositoryImpl
import it.thefedex87.networkresponsestateeventtest.domain.repository.CocktailRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideTheCocktailDbApi(): TheCocktailDbApi = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(TheCocktailDbApi.BASE_URL)
        .build()
        .create(TheCocktailDbApi::class.java)


    @Singleton
    @Provides
    fun provideCocktailRepository(cocktailDbApi: TheCocktailDbApi): CocktailRepository = CocktailRepositoryImpl(
        cocktailDbApi = cocktailDbApi
    )
}