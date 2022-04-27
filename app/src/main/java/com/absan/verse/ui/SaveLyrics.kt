package com.absan.verse.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import com.absan.verse.Utils.DatabaseRelated.DatabaseHandler
import com.absan.verse.data.SaveLyric__ItemAdapter

class SaveLyrics : DialogFragment() {

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment__savelyrics, container, false)
    }

    override fun onStart() {
        setAdapter(requireView().context)
        super.onStart()
    }

    private fun setAdapter(context: Context) {
        if (DatabaseHandler(context).readLyrics().size > 0) {
            val recycler = requireView().findViewById<RecyclerView>(R.id.savedLyrics_Recycler)
            recycler.layoutManager = LinearLayoutManager(context)
            val itemAdapter = SaveLyric__ItemAdapter(context, DatabaseHandler(context).readLyrics(),recycler)
            recycler.adapter = itemAdapter
        }

    }

}

