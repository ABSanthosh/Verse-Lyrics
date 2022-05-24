package com.absan.verse.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import kotlin.system.exitProcess

fun openSpotify(context: Context, songId: String = "") {
    try {
        if(songId == "") {
            val sendIntent = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
            if (sendIntent != null) {
                startActivity(context, sendIntent, null)
            }
        }else{
            val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(songId))
            startActivity(context, launcher, null)
        }

    } catch (e: java.lang.Exception) {
        startActivity(
            context,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
            ), null
        )
    }
    Toast.makeText(context, "Open verse again", Toast.LENGTH_LONG).show()
    exitProcess(0)
}