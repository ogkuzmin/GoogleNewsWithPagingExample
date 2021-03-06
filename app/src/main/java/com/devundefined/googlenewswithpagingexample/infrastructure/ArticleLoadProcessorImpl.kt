package com.devundefined.googlenewswithpagingexample.infrastructure

import android.annotation.SuppressLint
import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.loader.LoadResult
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.ArticleDto
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsApi
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class ArticleLoadProcessorImpl(private val newsApi: NewsApi, private val apiKey: String, private val country: String) :
    ArticleLoadProcessor {

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        private const val OK_RESULT = "ok"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val toModel: (ArticleDto) -> Article = { dto ->
        with(dto) { Article(source.name, author?: "", title, description?: "", url, imageUrl?: "", sdf.parse(date)) }
    }

    override fun processLoading(pageNumber: Int, pageSize: Int): LoadResult {
        return runBlocking {
            withContext(scope.coroutineContext) {
                try {
                    val result = newsApi.getNews(apiKey, country, pageNumber, pageSize)
                    if (result.status != OK_RESULT) {
                        LoadResult.Error(IllegalStateException(result.message))
                    } else {
                        LoadResult.Data(result.articles.map(toModel), result.totalResults!!, pageSize, pageNumber)
                    }
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }

            }
        }
    }
}