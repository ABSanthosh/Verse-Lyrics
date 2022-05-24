package com.absan.verse.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


fun MyName(context: Context){
    startActivity(
        context,
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://youtu.be/dQw4w9WgXcQ")
        ),null
    )
}