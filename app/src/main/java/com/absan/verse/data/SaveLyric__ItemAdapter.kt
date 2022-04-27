package com.absan.verse.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import com.absan.verse.Utils.DatabaseRelated.removeSong


class SaveLyric__ItemAdapter(
    val context: Context,
    private val songList: ArrayList<Song>,
    val recycler: RecyclerView
) :
    RecyclerView.Adapter<SaveLyric__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName = view.findViewById<TextView>(R.id.saveSong__Songname)
        val ArtistName = view.findViewById<TextView>(R.id.saveSong__Artistname)
        val SongID = view.findViewById<TextView>(R.id.saveSong__songId)
        val SongPos = view.findViewById<TextView>(R.id.saveSong__songPos)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.fragment__savelyrics_row, parent, false)

        view.findViewById<ImageView>(R.id.savedLyricsOptions).setOnClickListener {
            val popupMenu =
                PopupMenu(context, view.findViewById<ImageView>(R.id.savedLyricsOptions))
            popupMenu.menuInflater.inflate(R.menu.savedsongs__menu, popupMenu.menu)
            val songIdTextview = view.findViewById<TextView>(R.id.saveSong__songId)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popUpPlaySong -> {
                        OpenSongInSpotify(songIdTextview.text.toString())
                    }

                    R.id.popUpDeleteSong -> {
                        songList.forEach {
                            if (it.id == songIdTextview.text) {
                                removeSong(context, it)
                            }
                        }

                        recycler.removeViewAt(
                            Integer.parseInt(
                                view.findViewById<TextView>(
                                    R.id.saveSong__songPos
                                ).text.toString()
                            )
                        )

//                        (view.parent as RecyclerView).removeViewAt(
//                            Integer.parseInt(
//                                view.findViewById<TextView>(
//                                    R.id.saveSong__songPos
//                                ).text.toString()
//                            )
//                        )
                    }
                }
                true
            })
            popupMenu.show()
        }

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.SongID.text = item.id
        holder.songName.text = item.track
        holder.ArtistName.text = item.artist
        holder.SongPos.text = position.toString()
    }

    override fun getItemCount() = songList.size

    fun OpenSongInSpotify(id: String) {
        val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(id))
        startActivity(context, launcher, null)
    }
}
