package com.absan.verse.Helpers

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@SuppressLint("SetTextI18n")
suspend fun copyrightTextView(
    context: Context,
    googleQuery: String,
    isGoogle: Boolean = false
): TextView {
    var copyrightText = ""
    withContext(Dispatchers.IO) {
        val userAgent =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"


        if (!isGoogle) {
            val document: org.jsoup.nodes.Document? =
                Jsoup.connect(googleQuery).timeout(60 * 1000).userAgent(userAgent).get()
            val lyricDiv = document!!.select("body").text()
            copyrightText = try {
                "Lyrics licenced & provided by Musixmatch\n" +
                        JSONObject(lyricDiv)
                            .getJSONObject("message")
                            .getJSONObject("body")
                            .getJSONObject("macro_calls")
                            .getJSONObject("track.lyrics.get")
                            .getJSONObject("message")
                            .getJSONObject("body")
                            .getJSONObject("lyrics")
                            .getString("lyrics_copyright")
            } catch (error: Exception) {
                "Lyrics licenced & provided by Musixmatch"
            }
        } else {
//            try {
                val document: org.jsoup.nodes.Document? =
                    Jsoup.connect(googleQuery).timeout(60 * 1000).userAgent(userAgent).get()
                val lyricSource: Element =
                    document!!.select("div").select("[class='j04ED']").first()
                copyrightText = "${lyricSource.text()} \n"
                val lyricLicence: Elements = document.select("div").select("[class='auw0zb']")
                lyricLicence.forEachIndexed { _, element ->
                    copyrightText += "${element.text()} \n"
                }
//            } catch (error: Exception) {
//                copyrightText = "Lyrics licenced & provided by Musixmatch"
//            }
        }

    }
    val copyrightLine = TextView(context)
    copyrightLine.textAlignment = View.TEXT_ALIGNMENT_CENTER
    copyrightLine.text = "$copyrightText \n"
    val params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    copyrightLine.setTextColor(ContextCompat.getColor(context,R.color.textColor))
    params.setMargins(0, 0, 0, 15)
    copyrightLine.layoutParams = params
    copyrightLine.typeface = ResourcesCompat.getFont(context, R.font.walter_turncoat)
    copyrightLine.textSize = 19F
    copyrightLine.id = R.id.copyright
    return copyrightLine
}