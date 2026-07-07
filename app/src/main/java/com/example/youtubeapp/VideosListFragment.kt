package com.example.youtubeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

// Tab 1: list of videos from the API in a RecyclerView, with Options Menu,
// search, pull-to-refresh and a loading indicator
class VideosListFragment : Fragment() {

    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: VideoAdapter
    private lateinit var progress: ProgressBar
    private lateinit var swipe: SwipeRefreshLayout

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progress = view.findViewById(R.id.progressBar)
        swipe = view.findViewById(R.id.swipeRefresh)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerVideos)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = VideoAdapter { video ->
            // Pass the selected video to the Details fragment via the shared ViewModel
            vm.select(video)
            (requireActivity() as MainActivity).goToDetails()
        }
        recycler.adapter = adapter

        vm.videos.observe(viewLifecycleOwner) { adapter.setVideos(it) }
        swipe.setOnRefreshListener { fetchVideos() }

        if (vm.videos.value.isNullOrEmpty()) fetchVideos()
    }

    @Suppress("DEPRECATION")
    private fun fetchVideos(query: String = "android development tutorials") {
        progress.visibility = View.VISIBLE
        FetchVideosTask { result ->
            if (!isAdded) return@FetchVideosTask
            progress.visibility = View.GONE
            swipe.isRefreshing = false
            if (result != null && result.isNotEmpty()) {
                vm.setVideos(result)
                NotificationHelper.show(
                    requireContext(), "Tube Explorer", "Videos loaded successfully", 1
                )
            } else {
                NotificationHelper.show(
                    requireContext(), "Tube Explorer", "Error loading videos from API", 2
                )
            }
        }.execute(query)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Search videos..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                fetchVideos()
                true
            }
            R.id.action_about -> {
                NotificationHelper.show(
                    requireContext(), "Tube Explorer",
                    "MOB2 Final Project - YouTube Data API v3", 3
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
