package com.example.youtubeapp

import android.os.AsyncTask
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// AsyncTask that fetches videos from the YouTube Data API v3 in the background
@Suppress("DEPRECATION")
class FetchVideosTask(private val onResult: (List<Video>?) -> Unit) :
    AsyncTask<String, Void, List<Video>?>() {

    companion object {
        private const val API_KEY = "AIzaSyAEk7F_bbhTFUWxwJXDn5fzxviwCJYk7EY"
    }

    override fun doInBackground(vararg params: String): List<Video>? {
        return try {
            val q = URLEncoder.encode(params[0], "UTF-8")
            val url = URL(
                "https://www.googleapis.com/youtube/v3/search" +
                    "?part=snippet&type=video&maxResults=40" +
                    "&q=" + q + "&key=" + API_KEY
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            parse(text)
        } catch (e: Exception) {
            null
        }
    }

    override fun onPostExecute(result: List<Video>?) {
        onResult(result)
    }

    private fun parse(json: String): List<Video> {
        val list = ArrayList<Video>()
        val root = JSONObject(json)
        val items = root.optJSONArray("items") ?: return list
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            val videoId = item.optJSONObject("id")?.optString("videoId") ?: ""
            if (videoId.isEmpty()) continue
            val snippet = item.optJSONObject("snippet") ?: continue
            val thumbs = snippet.optJSONObject("thumbnails")
            val thumb = thumbs?.optJSONObject("high")?.optString("url")
                ?: thumbs?.optJSONObject("medium")?.optString("url")
                ?: thumbs?.optJSONObject("default")?.optString("url")
                ?: ""
            val published = snippet.optString("publishedAt", "N/A")
            list.add(
                Video(
                    videoId = videoId,
                    title = snippet.optString("title", "Untitled"),
                    channel = snippet.optString("channelTitle", "Unknown channel"),
                    publishedAt = if (published.length >= 10) published.substring(0, 10) else published,
                    description = snippet.optString("description", "No description available."),
                    thumbnail = thumb
                )
            )
        }
        return list
    }
}
