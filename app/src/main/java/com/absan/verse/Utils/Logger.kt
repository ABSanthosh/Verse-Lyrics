package com.absan.verse.Utils

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import com.absan.verse.data.Song
import com.absan.verse.data.Spotify
import com.absan.verse.data.isDuplicateOf
import kotlinx.coroutines.*

class Logger : Service() {
    private val audioManager by lazy { applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var lastSong = Song()

    @Volatile
    private var isMuted = false
    private var MUTEDELAY = 100
    private val UNMUTEDELAY = 100
    private val listener = CoroutineScope(Dispatchers.Default)
    private var running = false
    private val spotifyReceiver = Spotify.spotifyReceiver(::handleSongIntent)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Start", "Service started")
        when (intent?.action) {
            "START_FOREGROUND" -> startLoggerService()
            "STOP_SERVICE" -> stopSelf()
            "MUTE" -> if (getMusicVolume() == 0) actionUnmute() else actionMute()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.e("End", "Service Ended")
        unregisterReceiver(spotifyReceiver)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun getMusicVolume() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    private fun startLoggerService() {
        if (running) return
        registerReceiver(
            spotifyReceiver,
            Spotify.INTENT_FILTER
        )
        running = true
    }


    private fun handleSongIntent(song: Song) {
        if (song.isDuplicateOf(lastSong)) return
        Log.e("SongIntent", "log: $song")
        lastSong = song
        when {
            song.playing -> handleNewSongPlaying(song)
            else -> handleSongNotPlaying(song)
        }
    }

    private fun handleSongNotPlaying(song: Song) {
        Log.d("SongNotPlaying", "Handle song not playing")
        listener.coroutineContext.cancelChildren()
    }

    private fun getMuteDelay(): Int {
        return MUTEDELAY
    }

    private fun getUnuteDelay(): Int {
        return UNMUTEDELAY
    }

    private fun handleNewSongPlaying(newSong: Song) {
        Log.d("NewSong", "Handle song playing")
        listener.coroutineContext.cancelChildren()
        if (isMuted) {
            setUnmuteTimer(
                wait = getUnuteDelay() - newSong.playbackPosition - newSong.propagation()
            )
        }
        setMuteTimer(newSong.systemTimeLeft() + getMuteDelay())
    }

    private fun setUnmuteTimer(wait: Long) {
        Log.d(ContentValues.TAG, "Unmuting in $wait ms")
        listener.launch {
            delay(wait)
            unmute()
        }
    }

    @Synchronized
    private fun mute() {
        isMuted = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }
    }

    @Synchronized
    private fun unmute() {
        isMuted = false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_UNMUTE,
                0
            )
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        }
    }

    private fun actionMute() {
        listener.coroutineContext.cancelChildren()
        mute()
    }

    private fun actionUnmute() {
        listener.coroutineContext.cancelChildren()
        unmute()

        val timeLeft = lastSong.timeFinish - System.currentTimeMillis()
        if (timeLeft > 0) {
            setMuteTimer(timeLeft + getMuteDelay())
        }
    }

    private fun setMuteTimer(wait: Long) {
        Log.d(ContentValues.TAG, "Muting in $wait ms")
        listener.launch {
            delay(wait)
            mute()
            delay(2000L)
        }
    }

}