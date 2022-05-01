package com.absan.verse.Utils.DatabaseRelated

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.absan.verse.data.Song

class BookmarkDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "SavedLyrics"

        private val TABLE_LYRICS = "Lyrics"

        private val KEY_SONG_ID = "_id"
        private val KEY_SONG_NAME = "name"
        private val KEY_SONG_ARTIST = "artist"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LYRICS_TABLE =
            "CREATE TABLE $TABLE_LYRICS ($KEY_SONG_ID TEXT PRIMARY KEY, $KEY_SONG_NAME TEXT, $KEY_SONG_ARTIST TEXT)"

        db?.execSQL(CREATE_LYRICS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_LYRICS")
        onCreate(db)
    }

    fun addLyrics(song: Song): Long {
        val db = this.writableDatabase
        val contentValue = ContentValues().apply {
            put(KEY_SONG_ID, song.id)
            put(KEY_SONG_NAME, song.track)
            put(KEY_SONG_ARTIST, song.artist)
        }

        val response = db.insert(TABLE_LYRICS, null, contentValue)
        db.close()

        return response
    }

    @SuppressLint("Recycle", "Range")
    fun readLyrics(): ArrayList<Song> {
        val savedLyrics = ArrayList<Song>()

        val selectQuery = "SELECT * FROM $TABLE_LYRICS"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (error: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                savedLyrics.add(
                    Song(
                        id = cursor.getString(cursor.getColumnIndex(KEY_SONG_ID)),
                        track = cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)),
                        artist = cursor.getString(cursor.getColumnIndex(KEY_SONG_ARTIST))
                    )
                )
            } while (cursor.moveToNext())
        }

        db.close()
        cursor.close()
        return savedLyrics
    }

    fun removeLyrics(song: Song): Int {
        val db = this.writableDatabase
        val response = db.delete(TABLE_LYRICS, "$KEY_SONG_ID = '${song.id}'", null)
        db.close()
        return response
    }

    fun isAlreadySaved(song: Song):Boolean{
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_LYRICS WHERE _id = '${song.id}'"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if(cursor.count <= 0){
            cursor.close()
            return false
        }
        cursor.close()
        return true

    }
}