package com.example.myapplication.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Article
import com.example.myapplication.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()
    
    init {
        loadHeadlines()
        loadNews(refresh = true)
    }
    
    fun loadHeadlines() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    _state.update { it.copy(headlines = articles, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
    
    fun loadNews(refresh: Boolean = false) {
        viewModelScope.launch {
            val currentState = _state.value
            val page = if (refresh) 1 else currentState.currentPage
            
            if (refresh) {
                _state.update { it.copy(isLoading = true, news = emptyList(), currentPage = 1, isLastPage = false) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }
            
            repository.getNews(page)
                .onSuccess { articles ->
                    val newsList = if (refresh) articles else currentState.news + articles
                    val isLastPage = articles.isEmpty()
                    
                    _state.update { 
                        it.copy(
                            news = newsList, 
                            isLoading = false,
                            currentPage = page + 1,
                            isLastPage = isLastPage
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}