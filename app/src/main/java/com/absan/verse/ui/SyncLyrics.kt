package com.absan.verse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.absan.verse.R

class SyncLyrics : DialogFragment() {

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment__synclyrics, container, false)
    }

}

