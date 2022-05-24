package com.absan.verse.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

fun RateOnPlayStore(context: Context) {
    val uri = Uri.parse("market://details?id=com.absan.verse")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)

    try {
        startActivity(context, goToMarket, null)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            context,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=com.absan.verse")
            ), null
        )
    }
}