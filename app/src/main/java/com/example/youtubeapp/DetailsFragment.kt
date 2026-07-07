package com.example.youtubeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide

// Tab 2: details of the selected video (defaults to the first video if none selected)
class DetailsFragment : Fragment() {

    private val vm: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.selected.observe(viewLifecycleOwner) { sel ->
            render(view, sel ?: vm.videos.value?.firstOrNull())
        }
        vm.videos.observe(viewLifecycleOwner) { list ->
            if (vm.selected.value == null) render(view, list.firstOrNull())
        }
    }

    private fun render(view: View, video: Video?) {
        val thumb = view.findViewById<ImageView>(R.id.dThumb)
        val title = view.findViewById<TextView>(R.id.dTitle)
        val channel = view.findViewById<TextView>(R.id.dChannel)
        val date = view.findViewById<TextView>(R.id.dDate)
        val desc = view.findViewById<TextView>(R.id.dDescription)
        val btn = view.findViewById<Button>(R.id.btnWatch)

        if (video == null) {
            title.text = "No videos loaded yet"
            return
        }

        title.text = video.title
        channel.text = "Channel: " + video.channel
        date.text = "Published: " + video.publishedAt
        desc.text = video.description

        if (video.thumbnail.isNotEmpty()) {
            Glide.with(this).load(video.thumbnail).into(thumb)
        }

        btn.setOnClickListener {
            val watchUrl = "https://www.youtube.com/watch?v=" + video.videoId
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)))
        }
    }
}
