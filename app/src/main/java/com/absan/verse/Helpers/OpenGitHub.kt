package com.absan.verse.Helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

fun OpenGitHub(context: Context) {
    val browserIntent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ABSanthosh"))
    startActivity(context, browserIntent, null)
}