package com.example.youtubeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Locale

// RecyclerView adapter with built-in search filtering
class VideoAdapter(private val onClick: (Video) -> Unit) :
    RecyclerView.Adapter<VideoAdapter.VH>() {

    private var full: List<Video> = emptyList()
    private var shown: List<Video> = emptyList()

    fun setVideos(list: List<Video>) {
        full = list
        shown = list
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val q = query.lowercase(Locale.ROOT)
        shown = if (q.isBlank()) full else full.filter {
            it.title.lowercase(Locale.ROOT).contains(q) ||
                it.channel.lowercase(Locale.ROOT).contains(q)
        }
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val thumb: ImageView = v.findViewById(R.id.imgThumb)
        val title: TextView = v.findViewById(R.id.txtTitle)
        val channel: TextView = v.findViewById(R.id.txtChannel)
        val date: TextView = v.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VH(v)
    }

    override fun getItemCount() = shown.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val vid = shown[position]
        holder.title.text = vid.title
        holder.channel.text = vid.channel
        holder.date.text = vid.publishedAt
        if (vid.thumbnail.isNotEmpty()) {
            Glide.with(holder.itemView)
                .load(vid.thumbnail)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.thumb)
        } else {
            holder.thumb.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        holder.itemView.setOnClickListener { onClick(vid) }
    }
}
