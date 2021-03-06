package com.devundefined.googlenewswithpagingexample.di.modules

import android.content.Context
import androidx.room.Room
import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository
import com.devundefined.googlenewswithpagingexample.infrastructure.ArticleLoadProcessorImpl
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsApi
import com.devundefined.googlenewswithpagingexample.infrastructure.persistance.ArticleDao
import com.devundefined.googlenewswithpagingexample.infrastructure.persistance.ArticleDatabase
import com.devundefined.googlenewswithpagingexample.infrastructure.persistance.ArticleRepositoryImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class InfrastructureModule(
    private val apiKey: String,
    private val appContext: Context
) {

    companion object {
        private const val NAME_COUNTRY = "country"
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideLoadProcessor(newsApi: NewsApi, @Named(NAME_COUNTRY) country: String): ArticleLoadProcessor =
        ArticleLoadProcessorImpl(newsApi, apiKey, country)


    @Provides
    @Named(NAME_COUNTRY)
    fun provideCountry(): String = "us"

    @Provides
    @Singleton
    fun providerRepository(dao: ArticleDao): ArticleRepository = ArticleRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideDao(articleDatabase: ArticleDatabase): ArticleDao = articleDatabase.dao()

    @Provides
    @Singleton
    fun provideDatabase(): ArticleDatabase = Room.databaseBuilder(appContext, ArticleDatabase::class.java, "article-pages").build()
}