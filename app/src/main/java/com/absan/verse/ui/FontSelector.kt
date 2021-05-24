package com.absan.verse.ui

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.fragment.app.DialogFragment
import com.absan.verse.R
import com.koolio.library.DownloadableFontList
import com.koolio.library.DownloadableFontList.FontListCallback
import com.koolio.library.FontList

class FontSelector : DialogFragment() {
    private val API_KEY = "AIzaSyAMBkvj7pFmtfP0--6Kery9A3ZayQHEWgE"
    private var mHandler: Handler? = null
    lateinit var textContent: TextView


    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val fontListCallback: FontListCallback = object : FontListCallback {
            override fun onFontListRetrieved(fontList: FontList) {
                val listView = view!!.findViewById<ListView>(R.id.font__Listview)
                val progressBar: androidx.constraintlayout.widget.ConstraintLayout =
                    view!!.findViewById(R.id.progressBarHolder)

                textContent = view?.findViewById(R.id.testContent)!!
                listView!!.adapter = ArrayAdapter(
                    context!!.applicationContext,
                    R.layout.font__listitem, R.id.fontName, fontList.fontFamilyList.toTypedArray()
                )
                Toast.makeText(
                    activity!!.applicationContext,
                    "Fonts Loaded", Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.GONE

                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        startFontDownload(fontList.getFontByID(position).queryString)
                    }
                setListViewHeightBasedOnChildren(listView)

            }

            override fun onTypefaceRequestFailed(reason: Int) {
            }
        }

        DownloadableFontList.requestDownloadableFontList(fontListCallback, API_KEY)

        return inflater.inflate(R.layout.fragment__fontselector, container, false)
    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        var i = 0
        val len = listAdapter.count
        while (i < len) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
            i++
        }
        val params = listView.layoutParams
        params.height = (totalHeight
                + listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
    }

    private fun startFontDownload(query: String) {
        val request = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            query,
            R.array.com_google_android_gms_fonts_certs
        )
        val callback: FontsContractCompat.FontRequestCallback =
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface?) {
                    textContent.typeface = typeface
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Preview Font set to: ${query.split("name=")[1].split("&weight")[0]}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                override fun onTypefaceRequestFailed(reason: Int) {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Unable to fetch this font", Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        getHandlerThreadHandler()?.let {
            FontsContractCompat
                .requestFont(
                    activity!!.applicationContext, request, callback,
                    it
                )
        }
    }

    private fun getHandlerThreadHandler(): Handler? {
        if (mHandler == null) {
            val handlerThread = HandlerThread("fonts")
            handlerThread.start()
            mHandler = Handler(handlerThread.looper)
        }
        return mHandler
    }

}