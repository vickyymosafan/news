package com.example.myapplication.data.model

import com.example.myapplication.data.model.Article

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)