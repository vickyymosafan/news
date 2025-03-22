class NewsRepository(private val apiService: NewsApiService) {
    suspend fun getTopHeadlines(): Result<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines()
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNews(page: Int): Result<List<Article>> {
        return try {
            val response = apiService.getEverything(page = page)
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}