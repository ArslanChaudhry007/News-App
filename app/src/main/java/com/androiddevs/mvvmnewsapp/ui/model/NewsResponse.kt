package com.androiddevs.mvvmnewsapp.ui.model

import com.androiddevs.mvvmnewsapp.ui.model.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)