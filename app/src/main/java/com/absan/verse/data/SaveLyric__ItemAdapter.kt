package com.absan.verse.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import com.absan.verse.Utils.DatabaseRelated.BookmarkDatabaseHandler
import com.absan.verse.Utils.DatabaseRelated.removeSong


class SaveLyric__ItemAdapter(
    val context: Context,
    private var songList: ArrayList<Song>,
    val recycler: RecyclerView,
    val messageText: TextView
) :
    RecyclerView.Adapter<SaveLyric__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName = view.findViewById<TextView>(R.id.saveSong__Songname)
        val ArtistName = view.findViewById<TextView>(R.id.saveSong__Artistname)
        val SongID = view.findViewById<TextView>(R.id.saveSong__songId)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.savelyrics__item, parent, false)

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

                        songList = BookmarkDatabaseHandler(context).readLyrics()


                        if (songList.size == 0) {
                            messageText.visibility = View.VISIBLE
                        }

                        val viewPos = recycler.indexOfChild(view)
                        recycler.removeView(view)
                        notifyItemRemoved(viewPos)

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
    }

    override fun getItemCount() = songList.size

    fun OpenSongInSpotify(id: String) {
        val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(id))
        startActivity(context, launcher, null)
    }
}
