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