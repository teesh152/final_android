package com.example.youtubeapp

import java.io.Serializable

// Data model for a single video fetched from the YouTube Data API v3
data class Video(
    val videoId: String,
    val title: String,
    val channel: String,
    val publishedAt: String,
    val description: String,
    val thumbnail: String
) : Serializable
