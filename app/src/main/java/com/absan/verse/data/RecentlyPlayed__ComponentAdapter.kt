package com.absan.verse.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RecentlyPlayed__ComponentAdapter(
    val context: Context,
    private var songList: HashMap<String, ArrayList<Song>>
) : RecyclerView.Adapter<RecentlyPlayed__ComponentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeStamp: TextView = view.findViewById(R.id.recentlyPlayed__Parent_TimeStamp)
        val songListRecyclerView: RecyclerView = view.findViewById(R.id.recentlyPlayed__Parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recentlyplayed__parent, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = songList.keys.elementAt(songList.keys.size - position - 1)
        val day = SimpleDateFormat("EE", Locale.getDefault()).format(Calendar.getInstance().time)
        val date = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(Date()).split("/")
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())

        holder.timeStamp.text = if ("$day, $month ${date[0]}, ${date[2]}" == key) "Today" else key

        holder.songListRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.songListRecyclerView.adapter =
            songList[key]?.let { RecentlyPlayed__ItemAdapter(context, it) }
    }

    override fun getItemCount() = songList.size
}