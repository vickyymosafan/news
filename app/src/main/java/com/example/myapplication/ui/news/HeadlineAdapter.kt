class HeadlineAdapter(private val onItemClick: (Article) -> Unit) : 
    ListAdapter<Article, HeadlineAdapter.HeadlineViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHeadlineBinding.inflate(inflater, parent, false)
        return HeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HeadlineViewHolder(private val binding: ItemHeadlineBinding) : 
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

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}