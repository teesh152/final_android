package com.example.youtubeapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Shared ViewModel used to pass data between the two fragments
class SharedViewModel : ViewModel() {

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _selected = MutableLiveData<Video?>(null)
    val selected: LiveData<Video?> = _selected

    fun setVideos(list: List<Video>) { _videos.value = list }

    fun select(video: Video) { _selected.value = video }
}
