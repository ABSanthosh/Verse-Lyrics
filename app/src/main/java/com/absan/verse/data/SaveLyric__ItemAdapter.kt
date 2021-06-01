package com.absan.verse.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R


class SaveLyric__ItemAdapter(val context: Context, private val songList: ArrayList<Song>) :
    RecyclerView.Adapter<SaveLyric__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName = view.findViewById<TextView>(R.id.saveSong__Songname)
        val ArtistName = view.findViewById<TextView>(R.id.saveSong__Artistname)
        val SongID = view.findViewById<TextView>(R.id.saveSong__songId)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.fragment__savelyrics_row, parent, false)
        view.setOnClickListener {
            val songIdTextview = view.findViewById<TextView>(R.id.saveSong__songId)
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
        startActivity(context, launcher, null)
    }
}
