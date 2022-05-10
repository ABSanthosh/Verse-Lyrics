package com.absan.verse.Utils.DatabaseRelated

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.absan.verse.data.Song
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RecentlyPlayedDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "RecentlyPlayed"

        private val TABLE_PLAYED = "Lyrics"

        private val KEY_SONG_ID = "_id"
        private val KEY_SONG_NAME = "name"
        private val KEY_SONG_ARTIST = "artist"

        private val KEY_PLAY_DAY = "day"
        private val KEY_PLAY_MONTH = "month"
        private val KEY_PLAY_DATE = "date"
        private val KEY_PLAY_YEAR = "year"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LYRICS_TABLE =
            "CREATE TABLE $TABLE_PLAYED ($KEY_SONG_ID TEXT PRIMARY KEY, $KEY_SONG_NAME TEXT, $KEY_SONG_ARTIST TEXT, $KEY_PLAY_DAY TEXT, $KEY_PLAY_MONTH TEXT, $KEY_PLAY_DATE TEXT, $KEY_PLAY_YEAR TEXT)"

        db?.execSQL(CREATE_LYRICS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYED")
        onCreate(db)
    }

    data class SongTimeStamped(
        val song: Song,
        val day: String,
        val month: String,
        val date: String,
        val year: String
    )

    fun addRecentlyPlayed(song: Song): Long {
        if (isAlreadyRecorded(song)) return 0

        val db = this.writableDatabase

        val day = SimpleDateFormat("EE", Locale.getDefault()).format(Calendar.getInstance().time)
        val date = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(Date()).split("/")
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
        val contentValue = ContentValues().apply {
            put(KEY_SONG_ID, song.id)
            put(KEY_SONG_NAME, song.track)
            put(KEY_SONG_ARTIST, song.artist)
            put(KEY_PLAY_DAY, day)
            put(KEY_PLAY_MONTH, month)
            put(KEY_PLAY_DATE, date[0])
            put(KEY_PLAY_YEAR, date[2])
        }

        val response = db.insert(TABLE_PLAYED, null, contentValue)
        db.close()

        return response
    }

    @SuppressLint("Range")
    fun readRecentlyPlayed(): HashMap<String, ArrayList<Song>> {
        val recentlyPlayed = ArrayList<SongTimeStamped>()

        val selectQuery = "SELECT * FROM ${TABLE_PLAYED}"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (error: SQLiteException) {
            db.execSQL(selectQuery)
            return HashMap()
        }

        if (cursor.moveToFirst()) {
            do {
                recentlyPlayed.add(
                    SongTimeStamped(
                        Song(
                            id = cursor.getString(cursor.getColumnIndex(KEY_SONG_ID)),
                            track = cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)),
                            artist = cursor.getString(cursor.getColumnIndex(KEY_SONG_ARTIST))
                        ),
                        day = cursor.getString(cursor.getColumnIndex(KEY_PLAY_DAY)),
                        date = cursor.getString(cursor.getColumnIndex(KEY_PLAY_DATE)),
                        month = cursor.getString(cursor.getColumnIndex(KEY_PLAY_MONTH)),
                        year = cursor.getString(cursor.getColumnIndex(KEY_PLAY_YEAR))
                    )
                )
            } while (cursor.moveToNext())
        }

        db.close()
        cursor.close()

        return dateSortedList(recentlyPlayed)
    }

    private fun dateSortedList(data: ArrayList<SongTimeStamped>): HashMap<String, ArrayList<Song>> {
        val mutableSongList = hashMapOf<String, ArrayList<Song>>()

        data.asReversed().forEach {
            val dKey = "${it.day}, ${it.month} ${it.date}, ${it.year}"
            if (!mutableSongList.containsKey(dKey)) {
                Log.e("Sort",dKey)
                mutableSongList[dKey] = arrayListOf(it.song)
            } else {
                mutableSongList[dKey]!!.add(0, it.song)
            }
        }
        return mutableSongList
    }

    private fun isAlreadyRecorded(song: Song): Boolean {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLAYED WHERE _id = '${song.id}'"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }
}