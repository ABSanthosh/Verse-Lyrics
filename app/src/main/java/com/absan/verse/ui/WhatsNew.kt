package com.absan.verse.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.R
import net.cachapa.expandablelayout.ExpandableLayout
import net.cachapa.expandablelayout.ExpandableLayout.OnExpansionUpdateListener

class WhatsNew : DialogFragment() {

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView: View = inflater.inflate(R.layout.fragment__whatsnew, container, false)
//        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = SimpleAdapter(recyclerView)

        return rootView
    }

//    private class SimpleAdapter(private val recyclerView: RecyclerView) :
//        RecyclerView.Adapter<SimpleAdapter.ViewHolder?>() {
//        private var selectedItem = -1
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView: View = LayoutInflater.from(parent.context)
//                .inflate(R.layout.whatsnew__item, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.bind()
//        }
//
//        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
//            View.OnClickListener, OnExpansionUpdateListener {
//            private val expandableLayout: ExpandableLayout =
//                itemView.findViewById(R.id.expandable_layout)
//            private val expandButton: TextView
//
//            @SuppressLint("SetTextI18n")
//            fun bind() {
//                val position: Int = bindingAdapterPosition
//                val isSelected = position == selectedItem
//                expandButton.text = "$position Tap to expand"
//                expandButton.isSelected = isSelected
//                expandableLayout.setExpanded(isSelected, false)
//            }
//
//            override fun onClick(view: View) {
//                val holder =
//                    recyclerView.findViewHolderForAdapterPosition(selectedItem) as? ViewHolder
//                holder?.expandButton?.isSelected = false
//                holder?.expandableLayout?.collapse()
//                val position: Int = bindingAdapterPosition
//                if (position == selectedItem) {
//                    selectedItem = -1
//                } else {
//                    expandButton.isSelected = true
//                    expandableLayout.expand()
//                    selectedItem = position
//                }
//            }
//
//            override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
//                if (state == ExpandableLayout.State.EXPANDING) {
//                    recyclerView.smoothScrollToPosition(bindingAdapterPosition)
//                }
//            }
//
//            init {
//                expandableLayout.setInterpolator(OvershootInterpolator())
//                expandableLayout.setOnExpansionUpdateListener(this)
//                expandButton = itemView.findViewById(R.id.expand_button)
//                expandButton.setOnClickListener(this)
//            }
//        }
//
//
//        override fun getItemCount(): Int {
//            return 5
//        }
//
//    }

}