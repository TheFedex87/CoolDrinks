package it.thefedex87.cooldrinks.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.thefedex87.cooldrinks.data.local.CoolDrinkDatabase
import it.thefedex87.cooldrinks.data.local.DrinkDao
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.data.repository.CocktailRepositoryImpl
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
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
    fun provideDatabase(app: Application): CoolDrinkDatabase =
        Room.databaseBuilder(
            app,
            CoolDrinkDatabase::class.java,
            "cool_drink_db"
        ).build()


    @Singleton
    @Provides
    fun provideDrinkDao(db: CoolDrinkDatabase) =
        db.drinkDao

    @Singleton
    @Provides
    fun provideCocktailRepository(
        cocktailDbApi: TheCocktailDbApi,
        drinkDao: DrinkDao
    ): CocktailRepository =
        CocktailRepositoryImpl(
            cocktailDbApi = cocktailDbApi,
            drinkDao = drinkDao
        )
}