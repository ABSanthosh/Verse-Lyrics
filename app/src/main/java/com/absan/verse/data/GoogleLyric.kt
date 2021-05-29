package com.absan.verse.data

import android.content.Context
import android.util.Log
import android.widget.TableLayout
import android.widget.TextView
import com.absan.verse.R
import com.absan.verse.Utils.copyrightTextView
import com.absan.verse.Utils.generateTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.net.URLEncoder


suspend fun GoogleLyric(
    song: Song,
    view: TableLayout,
    context: Context
) {
    val artistName = song.artist
    val songName = song.track

    val googleQuery = "https://www.google.com/search?q=${
        URLEncoder.encode(songName, "utf-8")
    }+${URLEncoder.encode(artistName, "utf-8")}+lyrics"
    val userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"


    withContext(Dispatchers.IO) {
        try {
            val document: org.jsoup.nodes.Document? =
                Jsoup.connect(googleQuery).timeout(60 * 1000).userAgent(userAgent).get()

            val lyricDiv = document!!.select("span").select("[jsname='YS01Ge']").first() != null
            if (lyricDiv) {
                Log.e("Protocol", "Google")
                val element: Elements = document.select("span").select("[jsname='YS01Ge']")
                if (view.findViewById<TextView>(R.id.copyright) == null) {
                    withContext(Dispatchers.Main) {
                        element.forEachIndexed { _, line ->
                            view.addView(generateTextView(context, line.text()))
                        }
                        view.addView(copyrightTextView(context, googleQuery, true))


                    }
                }

            } else {
                Log.e("Protocol", "Musixmatch")
                MusixmatchNormalLyric(
                    song = song,
                    view = view,
                    context = context
                )

            }

        }catch (err:Exception){}
    }
}
