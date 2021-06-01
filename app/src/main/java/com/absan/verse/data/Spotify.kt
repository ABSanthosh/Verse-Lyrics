package com.absan.verse.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter


class Spotify {
    companion object {
        private const val SPOTIFY_PACKAGE = "com.spotify.music"
        private const val PLAYBACK_STATE_CHANGED = "$SPOTIFY_PACKAGE.playbackstatechanged"
        private const val QUEUE_CHANGED = "$SPOTIFY_PACKAGE.queuechanged"
        private const val METADATA_CHANGED = "$SPOTIFY_PACKAGE.metadatachanged"

        var INTENT_FILTER = IntentFilter().apply {
            addAction(this@Companion.PLAYBACK_STATE_CHANGED)
        }

        fun spotifyReceiver(callback: ((Song) -> Unit)): BroadcastReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent == null) return
                    SongFromIntent(intent)?.let { callback(it) }
                }
            }

        fun SongFromIntent(intent: Intent): Song? {
            with(intent) {
                try {
                    val song = Song(
                        getStringExtra("id") ?: "",
                        getStringExtra("artist") ?: "",
                        getStringExtra("album") ?: "",
                        getStringExtra("track") ?: "",
                        length = getIntExtra("length", -1),
                        playbackPosition = getIntExtra("playbackPosition", -1),
                        playing = intent.getBooleanExtra("playing", false),
                        timeSent = getLongExtra("timeSent", -1L),
                        registeredTime = System.currentTimeMillis()
                    )

                    return if (song.id.isEmpty()) null else song
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            return null
        }
    }
}