package com.absan.verse.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.absan.verse.R
import com.absan.verse.Utils.ResetLyricView
import com.absan.verse.Utils.requestCustomFont
import com.absan.verse.data.Constants.TYPEFACE


class FontSelector : DialogFragment() {
    lateinit var textContent: TextView
    private val mainPrefInstance by lazy {
        activity!!.applicationContext.getSharedPreferences(
            "main",
            Context.MODE_PRIVATE
        )
    }
    private lateinit var mHandler: Handler

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment__fontselector, container, false)
    }

    override fun onStart() {

        textContent = view?.findViewById(R.id.testContent)!!
        view!!.findViewById<Button>(R.id.setDefault).setOnClickListener {
            mainPrefInstance.edit().apply {
                putString("FontQuery", null)
            }.apply()
            TYPEFACE = Typeface.DEFAULT
            ResetLyricView(activity!!.findViewById(R.id.lyricsContainer), context)
            textContent.setTypeface(ResourcesCompat.getFont(context!!, R.font.walter_turncoat))
        }

        view!!.findViewById<Button>(R.id.setFont).setOnClickListener {
            if (TYPEFACE != Typeface.DEFAULT) ResetLyricView(activity!!.findViewById(R.id.lyricsContainer))
            TYPEFACE = textContent.typeface
            ResetLyricView(activity!!.findViewById(R.id.lyricsContainer), context)
        }


        val handlerThread = HandlerThread("fonts")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        val listView = view!!.findViewById<TableLayout>(R.id.font__Listview)

        if (TYPEFACE != Typeface.DEFAULT) textContent.typeface = TYPEFACE

        val sizeInDPHeight = 50
        val sizeInDPMargin = 20
        val HeightInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeInDPHeight.toFloat(), resources
                .displayMetrics
        ).toInt()
        val MarginInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeInDPMargin.toFloat(), resources
                .displayMetrics
        ).toInt()

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            HeightInDp
        )
        params.setMargins(MarginInDp, 0, MarginInDp, 0)
        val outval = TypedValue()
        context!!.theme.resolveAttribute(android.R.attr.selectableItemBackground, outval, true)
        val adview = Thread {
            resources.getStringArray(R.array.family_names).forEachIndexed { _, s ->
                val fontName = TextView(context)
                fontName.text = s
                fontName.textSize = 24F
                fontName.layoutParams = params
                fontName.setPadding(MarginInDp, 0, 0, 0)
                fontName.setTextColor(Color.parseColor("#000000"))
                fontName.isClickable = true
                fontName.isFocusable = true
                fontName.gravity = Gravity.CENTER_VERTICAL
                fontName.setBackgroundResource(outval.resourceId)
                fontName.setOnClickListener {
                    requestCustomFont(
                        context = context!!,
                        familyName = (fontName.text).toString(),
                        mHandler = mHandler,
                        textView = textContent,
                        setConstant = false
                    )
                }
                   activity?.runOnUiThread { listView.addView(fontName)}

            }
        }
        if(adview.isAlive){
            adview.run()
        }else{
            adview.start()
        }


        super.onStart()
    }
}
