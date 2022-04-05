package com.absan.verse

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.*
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
    private var selectedFontName = ""
    private var selectedFontSize = 24f

    fun setFontSize(value: Float, previewText: TextView?) {
        when (value) {
            0f -> {
                previewText?.text = "Grape"
                previewText?.textSize = 16f
                selectedFontSize = 16f
            }
            25f -> {
                previewText?.text = "Apple"
                previewText?.textSize = 20f
                selectedFontSize = 20f
            }
            50f -> {
                previewText?.text = "Mango"
                previewText?.textSize = 24f
                selectedFontSize = 24f
            }
            75f -> {
                previewText?.text = "Banana"
                previewText?.textSize = 32f
                selectedFontSize = 32f
            }
            100f -> {
                previewText?.text = "Pineapple"
                previewText?.textSize = 38f
                selectedFontSize = 38f
            }
        }
    }

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

            val sizeSlider =
                textSizeModal.findViewById<com.google.android.material.slider.Slider>(R.id.setting__textSize__slider)

            val closeSheet = textSizeModal.findViewById<Button>(R.id.setting__textSize__close)
            val previewText = textSizeModal.findViewById<TextView>(R.id.setting__textSize__preview)

            requestCustomFont(
                context = this,
                familyName = mainPrefInstance.getString("FontQuery", null).toString(),
                mHandler = mHandler,
                textView = previewText,
                setConstant = false,
                isPutString = false
            )

            when (mainPrefInstance.getFloat("FontSize", 24f)) {
                16f -> sizeSlider?.value = 0f
                20f -> sizeSlider?.value = 25f
                24f -> sizeSlider?.value = 50f
                32f -> sizeSlider?.value = 75f
                38f -> sizeSlider?.value = 100f
            }

            setFontSize(sizeSlider!!.value, previewText)

            sizeSlider.addOnChangeListener { sizeSlider, value, fromUser ->
                setFontSize(value, previewText)
            }

            defaultButton?.setOnClickListener {
                mainPrefInstance.edit().apply {
                    putFloat("FontSize", 24f)
                }.apply()
                Constants.FONTSIZE = 24f
                setFontSize(50f, previewText)
                sizeSlider.value = 50f
            }

            saveButton?.setOnClickListener {
                mainPrefInstance.edit().apply {
                    putFloat("FontSize", selectedFontSize)
                }.apply()
                Constants.FONTSIZE = selectedFontSize
            }

            closeSheet?.setOnClickListener {
                textSizeModal.dismiss()
            }

            textSizeModal.setCancelable(false)
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

            Timer("SettingUp", false).schedule(100) {
                fontRoller?.selectedItemPosition =
                    fontList.indexOf(mainPrefInstance.getString("FontQuery", null))

            }
            requestCustomFont(
                context = this,
                familyName = mainPrefInstance.getString("FontQuery", null).toString(),
                mHandler = mHandler,
                textView = textContent,
                setConstant = false,
                isPutString = false,
            )

            fontSelectorModal.findViewById<Button>(R.id.setDefault)?.setOnClickListener {
                mainPrefInstance.edit().apply {
                    putString("FontQuery", "Walter Turncoat")
                }.apply()
                Constants.TYPEFACE = ResourcesCompat.getFont(this, R.font.walter_turncoat)!!
                textContent.typeface = ResourcesCompat.getFont(this, R.font.walter_turncoat)

                requestCustomFont(
                    context = this,
                    familyName = "Walter Turncoat",
                    mHandler = mHandler,
                    textView = textContent,
                    setConstant = true,
                    isPutString = true,
                )
            }

            fontSelectorModal.findViewById<Button>(R.id.setting__textFont__close)
                ?.setOnClickListener {
                    fontSelectorModal.dismiss()
                }

            fontSelectorModal.findViewById<Button>(R.id.setFont)?.setOnClickListener {
                requestCustomFont(
                    context = this,
                    familyName = selectedFontName,
                    mHandler = mHandler,
                    textView = textContent,
                    setConstant = true,
                    isPutString = true,
                )
//                Constants.TYPEFACE = textContent.typeface
            }

            fontRoller?.setOnItemSelectedListener { fontRoller, fontList, position ->
                run {

                    val fontListData =
                        resources.getStringArray(R.array.family_names).toMutableList()

                    selectedFontName = fontListData.get(position)

                    requestCustomFont(
                        context = this,
                        familyName = fontListData.get(position),
                        mHandler = mHandler,
                        textView = textContent,
                        setConstant = false,
                        isPutString = false
                    )
                }
            }

            fontSelectorModal.setCancelable(false)
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