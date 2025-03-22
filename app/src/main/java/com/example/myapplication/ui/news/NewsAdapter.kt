package com.example.myapplication.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.model.Article
import com.example.myapplication.databinding.ItemNewsBinding
import com.example.myapplication.databinding.LoadingFooterBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NewsAdapter(private val onItemClick: (Article) -> Unit) : 
    ListAdapter<Article, RecyclerView.ViewHolder>(ArticleDiffCallback()) {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = ItemNewsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                NewsViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val binding = LoadingFooterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            holder.bind(getItem(position))
        }
        // Loading view holder doesn't need binding
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount && isLoadingAdded && position == itemCount - 1) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    fun addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true
            submitList(currentList + null)
        }
    }

    fun removeLoadingFooter() {
        if (isLoadingAdded) {
            isLoadingAdded = false
            val newList = currentList.toMutableList()
            if (newList.isNotEmpty()) {
                newList.removeAt(newList.size - 1)
            }
            submitList(newList)
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(article: Article) {
            binding.titleTextView.text = article.title
            binding.descriptionTextView.text = article.description ?: "No description available"
            binding.sourceTextView.text = "${article.source.name} • ${formatDate(article.publishedAt)}"
            
            // Load image with Glide
            Glide.with(binding.imageView)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .into(binding.imageView)
        }
        
        private fun formatDate(dateString: String): String {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString) ?: return "Unknown time"
                val now = Calendar.getInstance().time
                
                val diffInMillis = now.time - date.time
                val diffInHours = diffInMillis / (1000 * 60 * 60)
                
                return when {
                    diffInHours < 1 -> "Just now"
                    diffInHours < 24 -> "$diffInHours hours ago"
                    else -> "${diffInHours / 24} days ago"
                }
            } catch (e: Exception) {
                return "Unknown time"
            }
        }
    }

    class LoadingViewHolder(binding: LoadingFooterBinding) : 
        RecyclerView.ViewHolder(binding.root)

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}