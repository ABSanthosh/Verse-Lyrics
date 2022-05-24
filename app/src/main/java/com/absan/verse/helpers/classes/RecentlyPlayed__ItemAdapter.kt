package com.absan.verse.helpers.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import com.absan.verse.helpers.openSpotify
import com.absan.verse.helpers.data.Song

class RecentlyPlayed__ItemAdapter(
    val context: Context,
    private var songList: ArrayList<Song>
) : RecyclerView.Adapter<RecentlyPlayed__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.recentlyPlayed__songName)
        val artistName: TextView = view.findViewById(R.id.recentlyPlayed__artistName)
        val songID: TextView = view.findViewById(R.id.recentlyPlayed__songId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recentlyplayed__child, parent, false)

        val songIdTextview = view.findViewById<TextView>(R.id.recentlyPlayed__songId)

        view.findViewById<ImageView>(R.id.recentlyPlayed__openSpotify).setOnClickListener {
            openSpotify(context, songIdTextview.text.toString())
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.songID.text = item.id
        holder.songName.text = item.track
        holder.artistName.text = item.artist
    }

    override fun getItemCount() = songList.size
}