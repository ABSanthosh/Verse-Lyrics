package com.absan.verse.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R

class RecentlyPlayed__ItemAdapter(
    val context: Context,
    private var songList: ArrayList<Song>
) : RecyclerView.Adapter<RecentlyPlayed__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName = view.findViewById<TextView>(R.id.recentlyPlayed__songName)
        val ArtistName = view.findViewById<TextView>(R.id.recentlyPlayed__artistName)
        val SongID = view.findViewById<TextView>(R.id.recentlyPlayed__songId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recentlyplayed__child, parent, false)

        val songIdTextview = view.findViewById<TextView>(R.id.recentlyPlayed__songId)
        view.findViewById<ImageView>(R.id.recentlyPlayed__openSpotify).setOnClickListener {
            OpenSongInSpotify(songIdTextview.text.toString())
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.SongID.text = item.id
        holder.songName.text = item.track
        holder.ArtistName.text = item.artist
    }

    override fun getItemCount() = songList.size

    fun OpenSongInSpotify(id: String) {
        val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(id))
        ContextCompat.startActivity(context, launcher, null)
    }
}