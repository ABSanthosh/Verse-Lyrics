package com.absan.verse.Utils.DatabaseRelated

import android.content.Context
import android.widget.Toast
import com.absan.verse.data.Song

fun addSong(context: Context, song: Song) {
    val databaseHandler = DatabaseHandler(context)
    if (arrayOf(song.track, song.id, song.artist).any { it -> it != "" }) {
        val status = databaseHandler.addLyrics(song)
        if (status < -1) {
            Toast.makeText(context, "Lyrics for ${song.track} saved", Toast.LENGTH_LONG).show()
        }
    }
}

fun removeSong(context: Context, song: Song) {
    val status = DatabaseHandler(context).removeLyrics(song)
    if (status < -1) {
        Toast.makeText(context, "Lyrics for ${song.track} removed", Toast.LENGTH_LONG).show()
    }
}
