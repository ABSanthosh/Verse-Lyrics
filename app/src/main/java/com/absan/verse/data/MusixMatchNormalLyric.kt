package com.absan.verse.data

import android.content.Context
import android.widget.TableLayout
import android.widget.TextView
import com.absan.verse.R
import com.absan.verse.Utils.copyrightTextView
import com.absan.verse.Utils.generateTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder

suspend fun MusixmatchNormalLyric(
    song: Song,
    view: TableLayout,
    context: Context
) {

    val albumName = song.album
    val artistName = song.artist
    val artistsName = song.artist
    val trackName = song.track
    val songId = song.id
    val userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"

    val userTokenList = mutableListOf(
        "210112990dc32665215f58362e1388fb29fefcee84d1957a2a3da1",
        "210118c17557662a82c131cd471530683eafb1121f26c838fc7d3b",
        "21011884321ecea0403cb27153484af7384ed538c0d59ba4fcbe86",
        "2101185ee72d12fc9e10c69d0f2bbecc07a3ae09301b28c0dfc916",
        "210118843620ff2e670a04c841fb7aacd9d39f1c45e4cca1ae8588",
        "210118f1b42762804e58437dc9c880bb38d649366e93e65e5795d1",
        "21011894c4e64b0bbe4b3c7974208f8ca9171fc40fd8f39d389d41",
        "21011810aa17df4b66aa1e83439e2af7ae5d324cc20109f002c76a",
        "210118b1426b20b7a41bab5aae1a40a75a00fa972706ba4500e958",
        "21011885a5537210b803e2cde22710017848c392709ac9a8f8658e",
        "210526086118cea30b65e5a0379401520eea4a12869342ea91ae25",
        "2105265dfe2e577946119ac52928b056daa65506f2147174bbcd59"
    )

    val userToken = userTokenList.random()

    val googleQuery = "https://apic-desktop.musixmatch.com/ws/1.1/macro.subtitles.get?" +
            "format=json&" +
            "namespace=lyrics_synched&" +
            "part=lyrics_crowd%2Cuser%2Clyrics_verified_by&" +
            "q_album=${URLEncoder.encode(albumName, " utf -8")}&" +
            "q_artist=${URLEncoder.encode(artistName, " utf -8")}&" +
            "q_artists=${URLEncoder.encode(artistsName, " utf -8")}&" +
            "q_track=${URLEncoder.encode(trackName, " utf -8")}&" +
            "user_language=en&" +
            "tags=nowplaying&" +
            "track_spotify_id=spotify%3Atrack%3A$songId&" +
            "f_subtitle_length_max_deviation=1&" +
            "subtitle_format=mxm&" +
            "app_id=web-desktop-app-v1.0&" +
            "usertoken=$userToken&" +
            "guid=b60737ef-880f-4262-98cb-9c0c3f1d736b&" +
            "signature=9uqG34C5SPnXUjFJTGzJdlyM%2BYI%3D&" +
            "signature_protocol=sha1"

    // Log.e("Query", googleQuery)
    try {
        withContext(Dispatchers.IO) {
            val document: org.jsoup.nodes.Document? =
                Jsoup.connect(googleQuery).timeout(60 * 1000).userAgent(userAgent).get()

            val lyricDiv = document!!.select("body").text()
            val checker = JSONObject(lyricDiv)
                .getJSONObject("message")
                .getJSONObject("body")
                .getJSONObject("macro_calls")
                .getJSONObject("track.lyrics.get")
                .getJSONObject("message")
                .getJSONObject("header")
                .getInt("status_code")

            if (checker != 404) {
                val JSONobj =
                    JSONObject(lyricDiv)
                        .getJSONObject("message")
                        .getJSONObject("body")
                        .getJSONObject("macro_calls")
                        .getJSONObject("track.lyrics.get")
                        .getJSONObject("message")
                        .getJSONObject("body")
                        .getJSONObject("lyrics")
                        .getString("lyrics_body")
                        .split("\n")

                if (view.findViewById<TextView>(R.id.copyright) == null) {
                    withContext(Dispatchers.Main) {
                        JSONobj.forEachIndexed { _, c ->
                            view.addView(generateTextView(context, c))
                        }
                        view.addView(copyrightTextView(context, googleQuery))
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    if (view.childCount == 0) {
                        view.addView(generateTextView(context, "No Lyrics available :("))
                    }
                }
            }

        }
    } catch (error: Exception) {
    }
}