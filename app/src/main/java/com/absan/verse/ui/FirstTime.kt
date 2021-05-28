package com.absan.verse.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.absan.verse.R
import com.absan.verse.Utils.OpenSpotify

class FirstTime : DialogFragment() {
    private val mainPrefInstance by lazy { activity!!.applicationContext.getSharedPreferences("main", Context.MODE_PRIVATE) }
    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onStart() {
        val broadcastStatus = view?.findViewById<Button>(R.id.spotifyBroadcast)
        broadcastStatus?.setOnClickListener { OpenSpotify(view!!.context) }
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainPrefInstance.edit().apply {
            putBoolean("FirstTime",false)
        }.apply()

        return inflater.inflate(R.layout.fragment__firsttime, container, false)
    }

}

