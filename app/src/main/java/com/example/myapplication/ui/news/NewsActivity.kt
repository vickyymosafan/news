package com.example.myapplication.ui.news

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.model.Article
import com.example.myapplication.databinding.ActivityNewsBinding
import com.example.myapplication.ui.detail.DetailActivity
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private val viewModel: NewsViewModel by viewModels { NewsViewModelFactory() }
    
    private val headlineAdapter = HeadlineAdapter { article -> openDetail(article) }
    private val newsAdapter = NewsAdapter { article -> openDetail(article) }
    
    private var isLoading = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerViews() {
        binding.headlinesRecyclerView.adapter = headlineAdapter
        
        binding.newsRecyclerView.adapter = newsAdapter
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                
                if (!isLoading && !viewModel.state.value.isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        viewModel.loadNews(refresh = false)
                    }
                }
            }
        })
    }
    
    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHeadlines()
            viewModel.loadNews(refresh = true)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                headlineAdapter.submitList(state.headlines)
                newsAdapter.submitList(state.news)
                
                isLoading = state.isLoading
                binding.progressBar.isVisible = state.isLoading && state.news.isEmpty()
                binding.swipeRefreshLayout.isRefreshing = false
                
                state.error?.let { error ->
                    Toast.makeText(this@NewsActivity, error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun openDetail(article: Article) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ARTICLE_URL, article.url)
            putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.title)
        }
        startActivity(intent)
    }
}