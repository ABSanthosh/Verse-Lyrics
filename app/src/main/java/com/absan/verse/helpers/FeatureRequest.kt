package com.absan.verse.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

fun OpenGoogleForm(context: Context) {
    val browserIntent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/Zk8xBsBu9yDmfKNNA"))
    ContextCompat.startActivity(context, browserIntent, null)
}