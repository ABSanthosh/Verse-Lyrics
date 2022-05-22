package com.absan.verse.helpers.data

data class Song(
    val id: String = "",
    val artist: String = "",
    val album: String = "",
    val track: String = "",
    val length: Int = 0,
    val playbackPosition: Int = 0,
    val playing: Boolean = false,
    val timeSent: Long = 0L,
    val registeredTime: Long = 0L
) {
    fun propagation() = System.currentTimeMillis() - timeSent
    fun systemTimeLeft() = timeFinish - System.currentTimeMillis()
    val timeFinish = timeSent + (length - playbackPosition)
}

fun Song.isSongReset(prevSong: Song): Boolean {
    return this.id == prevSong.id &&
            this.playing &&
            this.playbackPosition < 1000 &&
            prevSong.playbackPosition > 3000 &&
            this.playbackPosition < prevSong.playbackPosition
}

fun Song.isDuplicateOf(old: Song): Boolean {
    return this.id == old.id &&             // If same song logged twice,
//            this.playing == old.playing // both playing or both paused,
            !this.isSongReset(old)         // and new song is _not_ reset
}