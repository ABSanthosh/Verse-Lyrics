package com.absan.verse.helpers.classes

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import com.absan.verse.helpers.data.Song
import com.absan.verse.helpers.data.isDuplicateOf
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
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }
    private val spotifyReceiver = Spotify.spotifyReceiver(::handleSongIntent)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_FOREGROUND" -> startLoggerService()
            "STOP_SERVICE" -> stopSelf()
            "MUTE" -> if (getMusicVolume() == 0) actionUnmute() else actionMute()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
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
        lastSong = song
        when {
            song.playing -> handleNewSongPlaying(song)
            else -> handleSongNotPlaying(song)
        }
    }

    private fun handleSongNotPlaying(song: Song) {
        listener.coroutineContext.cancelChildren()
    }

    private fun getMuteDelay(): Int {
        return MUTEDELAY
    }

    private fun getUnuteDelay(): Int {
        return UNMUTEDELAY
    }

    private fun handleNewSongPlaying(newSong: Song) {
        listener.coroutineContext.cancelChildren()
        if (isMuted) {
            setUnmuteTimer(
                wait = getUnuteDelay() - newSong.playbackPosition - newSong.propagation()
            )
        }
        setMuteTimer(newSong.systemTimeLeft() + getMuteDelay())
    }

    private fun setUnmuteTimer(wait: Long) {
        listener.launch {
            delay(wait)
            unmute()
            logAdMuted()
        }
    }


    @Synchronized
    private fun mute() {
        isMuted = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
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
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0)
        }
    }

    private fun logAdMuted() {
        var adsMutedCounter = mainPrefInstance.getInt("AdCount", 0)
        adsMutedCounter++
        mainPrefInstance.edit().apply { putInt("AdCount", adsMutedCounter) }.apply()
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
        listener.launch {
            delay(wait)
            mute()
            delay(2000L)
        }
    }

}