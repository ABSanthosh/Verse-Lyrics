package com.absan.verse

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.Utils.*
import com.absan.verse.data.Constants
import com.aigestudio.wheelpicker.WheelPicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.concurrent.schedule


class Settings : AppCompatActivity() {
    private var RickRollcount = 0
    private lateinit var mHandler: Handler
    lateinit var textContent: TextView
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.settings)
        super.onCreate(savedInstanceState)

        val handlerThread = HandlerThread("fonts")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        findViewById<LinearLayout>(R.id.setting__textSize).setOnClickListener {

            val textSizeModal = BottomSheetDialog(this)
            textSizeModal.setContentView(R.layout.settings__fontsizemodal)


            val defaultButton =
                textSizeModal.findViewById<Button>(R.id.Setting__textSize__defaultButton)

            val saveButton =
                textSizeModal.findViewById<Button>(R.id.Setting__textSize__saveButton)



            textSizeModal.setCancelable(false)

            defaultButton?.setOnClickListener {
                textSizeModal.dismiss()
            }

            textSizeModal.show()
        }

        findViewById<LinearLayout>(R.id.setting__textFont).setOnClickListener {
            val fontSelectorModal = BottomSheetDialog(this)
            fontSelectorModal.setContentView(R.layout.settings__fontselector)

            val fontRoller =
                fontSelectorModal.findViewById<WheelPicker>(R.id.setting__fontSelector__roller)

            val fontList = resources.getStringArray(R.array.family_names).toMutableList()

            textContent = fontSelectorModal.findViewById(R.id.setting__fontSelector__sample)!!

            fontRoller?.data = fontList

            Timer("SettingUp", false).schedule(300) {
                fontRoller?.selectedItemPosition =
                    fontList.indexOf(mainPrefInstance.getString("FontQuery", null))

            }
            requestCustomFont(
                context = this,
                familyName = mainPrefInstance.getString("FontQuery", null).toString(),
                mHandler = mHandler,
                textView = textContent,
                setConstant = false
            )

            fontSelectorModal.findViewById<Button>(R.id.setDefault)?.setOnClickListener {
                mainPrefInstance.edit().apply {
                    putString("FontQuery", null)
                }.apply()
                Constants.TYPEFACE = ResourcesCompat.getFont(this, R.font.walter_turncoat)!!
                textContent.typeface = ResourcesCompat.getFont(this, R.font.walter_turncoat)
            }

            fontSelectorModal.findViewById<Button>(R.id.setFont)?.setOnClickListener {
                Constants.TYPEFACE = textContent.typeface
            }


            fontRoller?.setOnItemSelectedListener { fontRoller, fontList, position ->
                run {

                    val fontListData =
                        resources.getStringArray(R.array.family_names).toMutableList()

                    requestCustomFont(
                        context = this,
                        familyName = fontListData.get(position),
                        mHandler = mHandler,
                        textView = textContent,
                        setConstant = false
                    )
                }
            }

            fontSelectorModal.show()
        }

        findViewById<ImageButton>(R.id.settingBackButton).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.setting__giveFeedback).setOnClickListener {
            SendFeedback(this)
        }

        findViewById<LinearLayout>(R.id.setting__openGithub).setOnClickListener {
            OpenGitHub(this)
        }

        findViewById<LinearLayout>(R.id.setting__featureRequest).setOnClickListener {
            OpenGoogleForm(this)
        }

        findViewById<LinearLayout>(R.id.setting__rateInPlaystore).setOnClickListener {
            RateOnPlayStore(this)
        }

        findViewById<LinearLayout>(R.id.setting__myName).setOnClickListener {
            RickRollcount++
            if (RickRollcount >= 7) {
                MyName(this)
                RickRollcount = 0
            }
        }


    }

    override fun onBackPressed() {
        try {
            finish();
        } catch (err: Exception) {
            super.onBackPressed()
        }
    }

}