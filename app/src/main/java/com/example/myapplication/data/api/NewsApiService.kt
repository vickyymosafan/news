package com.example.myapplication.data.api

import com.example.myapplication.data.model.NewsResponse
import com.example.myapplication.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): NewsResponse
    
    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String = "general",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): NewsResponse
}