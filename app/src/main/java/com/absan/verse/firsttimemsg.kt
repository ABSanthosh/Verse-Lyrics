package com.absan.verse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.absan.verse.R
import kotlinx.android.synthetic.main.dialogfrag.*

class firsttimemsg  : DialogFragment() {
    override fun onStart() {
        var openspot = helpopenspotify
        openspot.setOnClickListener { MainActivity.getInstance()?.openspotify() }
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.ftmsg,container,false)
    }


}