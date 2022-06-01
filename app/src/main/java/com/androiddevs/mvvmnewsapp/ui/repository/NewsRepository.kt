package com.androiddevs.mvvmnewsapp.ui.repository

import com.androiddevs.mvvmnewsapp.ui.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.ui.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.ui.model.Article

class NewsRepository(private val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBraekingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSaveNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}