package com.absan.verse.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import com.absan.verse.Helpers.Database.BookmarkDatabaseHandler
import com.absan.verse.Helpers.Database.removeSong
import com.absan.verse.Helpers.openSpotify

class SaveLyric__ItemAdapter(
    val context: Context,
    private var songList: ArrayList<Song>,
    private val recycler: RecyclerView,
    private val messageText: TextView
) :
    RecyclerView.Adapter<SaveLyric__ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.saveSong__Songname)
        val artistName: TextView = view.findViewById(R.id.saveSong__Artistname)
        val songID: TextView = view.findViewById(R.id.saveSong__songId)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.savelyrics__item, parent, false)

        view.findViewById<ImageView>(R.id.savedLyricsOptions).setOnClickListener {
            val popupMenu =
                PopupMenu(context, view.findViewById<ImageView>(R.id.savedLyricsOptions))
            popupMenu.menuInflater.inflate(R.menu.savedsongs__menu, popupMenu.menu)
            val songIdTextview = view.findViewById<TextView>(R.id.saveSong__songId)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popUpPlaySong -> {
                        openSpotify(context, songIdTextview.text.toString())
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
            }
            popupMenu.show()
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
