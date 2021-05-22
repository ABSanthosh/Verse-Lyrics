package com.absan.verse.Utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

fun SendFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    val recipients = arrayOf("a.b.santhosh02@gmail.com")
    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Verse app")
    intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
    intent.type = "text/html"
    intent.setPackage("com.google.android.gm")
    startActivity(context, Intent.createChooser(intent, "Send mail"), null)
}