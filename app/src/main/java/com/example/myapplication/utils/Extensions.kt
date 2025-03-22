object Extensions {
    fun String.formatDate(): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(this) ?: return "Unknown time"
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