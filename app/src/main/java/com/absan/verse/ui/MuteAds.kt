package com.absan.verse.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.absan.verse.R

class MuteAds : DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onStart() {
        view?.findViewById<Button>(R.id.MuteAd_spotmute)?.setOnClickListener { OpenPlaystore() }
        view?.findViewById<Button>(R.id.MuteAd_github)?.setOnClickListener { OpenGitHubSpotMute() }
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment__mute_ad, container, false)
    }

    fun OpenPlaystore() {
        val uri = Uri.parse("market://details?id=com.developments.samu.muteforspotify")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.developments.samu.muteforspotify")
                )
            )
        }
    }

    fun OpenGitHubSpotMute() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/oyvindsam/SpotMute"))
        startActivity(browserIntent)
    }
}