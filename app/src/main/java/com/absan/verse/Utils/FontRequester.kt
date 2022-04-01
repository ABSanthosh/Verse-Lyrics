package com.absan.verse.Utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import com.absan.verse.R
import com.absan.verse.data.Constants.TYPEFACE

fun requestCustomFont(
    context: Context,
    familyName: String,
    mHandler: Handler,
    textView: TextView? = null,
    setConstant: Boolean = true,
    isPutString: Boolean= true
) {
    val queryBuilder = QueryBuilder(familyName)
    val mainPrefInstance by lazy { context.getSharedPreferences("main", Context.MODE_PRIVATE) }
    val query = queryBuilder.build()

    val request = FontRequest(
        "com.google.android.gms.fonts",
        "com.google.android.gms",
        query,
        R.array.com_google_android_gms_fonts_certs
    )


    val callback = object : FontsContractCompat.FontRequestCallback() {
        override fun onTypefaceRetrieved(typeface: Typeface) {
            if(isPutString) mainPrefInstance.edit().apply { putString("FontQuery", query) }.apply()
            if (setConstant) TYPEFACE = typeface
            if (textView != null) textView.typeface = typeface
        }

        override fun onTypefaceRequestFailed(reason: Int) {
            Toast.makeText(context, "${reason}: Unable to fetch font", Toast.LENGTH_SHORT).show()
        }
    }

    if (familyName != "null") {
        FontsContractCompat
            .requestFont(context, request, callback, mHandler)
    }

}
