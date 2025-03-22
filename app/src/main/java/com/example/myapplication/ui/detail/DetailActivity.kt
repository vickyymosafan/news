class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    
    companion object {
        const val EXTRA_ARTICLE_URL = "extra_article_url"
        const val EXTRA_ARTICLE_TITLE = "extra_article_title"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val url = intent.getStringExtra(EXTRA_ARTICLE_URL) ?: return finish()
        val title = intent.getStringExtra(EXTRA_ARTICLE_TITLE) ?: "Article"
        
        setupToolbar(title)
        setupWebView(url)
    }
    
    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            this.title = title
        }
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupWebView(url: String) {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                }
                
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                }
            }
            
            loadUrl(url)
        }
    }
    
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}