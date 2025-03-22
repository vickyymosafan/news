package com.example.myapplication.ui.news

import com.example.myapplication.data.model.Article

data class NewsState(
    val headlines: List<Article> = emptyList(),
    val news: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val isLastPage: Boolean = false
)