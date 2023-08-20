package it.thefedex87.cooldrinks.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.thefedex87.cooldrinks.data.local.CoolDrinkDatabase
import it.thefedex87.cooldrinks.data.local.FavoriteDrinkDao
import it.thefedex87.cooldrinks.data.local.IngredientsDao
import it.thefedex87.cooldrinks.data.preferences.DefaultPreferencesManager
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.data.repository.CocktailRepositoryImpl
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
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
        )
            .addMigrations(CoolDrinkDatabase.MigrationFrom5To6)
            .build()

    @Singleton
    @Provides
    fun providePrefrencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return DefaultPreferencesManager(context = context)
    }

    @Singleton
    @Provides
    fun provideDrinkDao(db: CoolDrinkDatabase) =
        db.favoriteDrinkDao

    @Singleton
    @Provides
    fun provideIngredientDao(db: CoolDrinkDatabase) =
        db.ingredientDao

    @Singleton
    @Provides
    fun provideCocktailRepository(
        cocktailDbApi: TheCocktailDbApi,
        drinkDao: FavoriteDrinkDao,
        ingredientsDao: IngredientsDao,
        preferencesManager: PreferencesManager
    ): CocktailRepository =
        CocktailRepositoryImpl(
            cocktailDbApi = cocktailDbApi,
            drinkDao = drinkDao,
            ingredientDao = ingredientsDao,
            preferencesManager = preferencesManager
        )
}