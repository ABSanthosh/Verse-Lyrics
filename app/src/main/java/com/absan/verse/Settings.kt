package com.absan.verse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.helpers.*
import com.absan.verse.helpers.classes.Logger
import com.absan.verse.helpers.database.BookmarkDatabaseHandler
import com.absan.verse.helpers.database.RecentlyPlayedDatabaseHandler
import com.absan.verse.helpers.objects.Constants
import com.aigestudio.wheelpicker.WheelPicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import hakobastvatsatryan.DropdownTextView
import java.util.*
import kotlin.concurrent.schedule


class Settings : AppCompatActivity() {
    private var RickRollcount = 0
    private lateinit var mHandler: Handler
    lateinit var textContent: TextView
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }
    private var selectedFontName = ""
    private var selectedFontSize = 24f

    private val loggerServiceIntentForeground by lazy {
        Intent(
            "START_FOREGROUND",
            Uri.EMPTY,
            this,
            Logger::class.java
        )
    }

    private fun setFontSize(value: Float, previewText: TextView?) {
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

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity__settings)
        super.onCreate(savedInstanceState)


        val handlerThread = HandlerThread("fonts")
        val sharedEditor = mainPrefInstance.edit()
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        findViewById<CardView>(R.id.setting__whatsNew).setOnClickListener {
            val newStuff = BottomSheetDialog(this)
            newStuff.setContentView(R.layout.settings__whatsnewmodal)

            val whatsNewContent = hashMapOf<String, Array<String>>()
            whatsNewContent["3.0.0"] = arrayOf(
                "UI fixes",
                "Added what's new in navigation menu(Placed in setting currently)",
                "Added Rate app in Play Store",
                "Added System default theme option",
                "Added 'Identify Song' feature(Removed in latest update)",
                "Fixed Ad Mute",
                "Fixed version notation to match Play Store",
                "Fixed uncertainty in song name when switched between 'Identify song' and 'Lyrics' options.",
                "Removed old ester egg and added a new one"
            )
            whatsNewContent["3.1.0"] = arrayOf(
                "Fixed some typos",
                "Added toast message to 'Identify Song' option to show song name when Verse is not open(Removed in latest update)"
            )
            whatsNewContent["3.2.0"] = arrayOf(
                "UI tweaks",
                "Open the identified song in spotify if available(Removed in latest update)",
                "Removed the toast message for song name and added a new UI for the same.(Removed in latest update)"
            )
            whatsNewContent["3.0.1"] = arrayOf("Fixed Shared preferences")
            whatsNewContent["4.0.0"] = arrayOf(
                "Added fonts selector",
                "Synced lyrics(Beta)",
                "New App Icon",
                "New Ad muting mechanism",
                "Save lyrics feature added(Only works while online)",
                "New EasterEgg :)",
                "Lyrics to more songs than before(Not all though)",
            )

            val whatsNewString = hashMapOf<Int,Array<Int>>()
            whatsNewString[R.string.Ver3_0_0] = arrayOf(
                R.string.Ver3_0_0_1,
                R.string.Ver3_0_0_2,
                R.string.Ver3_0_0_3,
                R.string.Ver3_0_0_4,
                R.string.Ver3_0_0_5,
                R.string.Ver3_0_0_6,
                R.string.Ver3_0_0_7,
                R.string.Ver3_0_0_8,
                R.string.Ver3_0_0_9,
                R.string.Ver3_0_0_10,
            )
            whatsNewString[R.string.Ver3_1_0] = arrayOf(
                R.string.Ver3_1_0_1,
                R.string.Ver3_1_0_2
            )
            whatsNewString[R.string.Ver3_2_0] = arrayOf(
                R.string.Ver3_2_0_1,
                R.string.Ver3_2_0_2,
                R.string.Ver3_2_0_3,
                R.string.Ver3_2_0_4
            )
            whatsNewString[R.string.Ver4_0_0] = arrayOf(
                R.string.Ver4_0_0_1,
                R.string.Ver4_0_0_2,
                R.string.Ver4_0_0_3,
                R.string.Ver4_0_0_4,
                R.string.Ver4_0_0_5,
                R.string.Ver4_0_0_6,
                R.string.Ver4_0_0_7
            )

            val root = newStuff.findViewById<LinearLayout>(R.id.setting__whatsNewModalContainer)


            whatsNewString.keys.forEach {

                val dropdown = DropdownTextView.Builder(this)
                    .setTitleTextRes(it)
                    .setTitleTextColorRes(R.color.textColor)
                    .setTitleTextColorExpandedRes(R.color.textColor)
                    .setTitleTextSizeRes(22)
                    .setContentTextRes()
//                    .setContentTextColorRes(R.color.your_content_text_color)
                    .setTitleFontRes(R.font.manrope)
                    .setContentTextSizeRes(20)
                    .setContentFontRes(R.font.manrope)
                    .setArrowDrawableRes(R.drawable.ic_arrow)
                    .build()

            }

            newStuff.show()
        }

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
                familyName = mainPrefInstance.getString("FontQuery", "Walter Turncoat").toString(),
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

                Toast.makeText(this, "Font size set to default", Toast.LENGTH_SHORT)
                    .show()
            }

            saveButton?.setOnClickListener {
                mainPrefInstance.edit().apply {
                    putFloat("FontSize", selectedFontSize)
                }.apply()
                Constants.FONTSIZE = selectedFontSize
                Toast.makeText(this, "Font size set to $selectedFontSize", Toast.LENGTH_SHORT)
                    .show()
                textSizeModal.dismiss()
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
                    fontList.indexOf(mainPrefInstance.getString("FontQuery", "Walter Turncoat"))

            }
            requestCustomFont(
                context = this,
                familyName = mainPrefInstance.getString("FontQuery", "Walter Turncoat").toString(),
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

                fontRoller?.selectedItemPosition = fontList.indexOf("Walter Turncoat")
                selectedFontName = "Walter Turncoat"

                requestCustomFont(
                    context = this,
                    familyName = "Walter Turncoat",
                    mHandler = mHandler,
                    textView = textContent,
                    setConstant = true,
                    isPutString = true,
                )

                Toast.makeText(this, "Font set to default", Toast.LENGTH_SHORT)
                    .show()
                fontSelectorModal.dismiss()
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
                Toast.makeText(this, "Font face set to $selectedFontName", Toast.LENGTH_SHORT)
                    .show()
                fontSelectorModal.dismiss()
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

        findViewById<Switch>(R.id.setting__adBlockSwitch).isChecked =
            mainPrefInstance.getBoolean("MuteAd", false)

        findViewById<Switch>(R.id.setting__adBlockSwitch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.startService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service started",
                    Toast.LENGTH_SHORT
                ).show()
                sharedEditor.apply {
                    putBoolean("MuteAd", true)
                }.apply()
            } else {
                this.stopService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service ended",
                    Toast.LENGTH_SHORT
                ).show()
                sharedEditor.apply {
                    putBoolean("MuteAd", false)
                }.apply()
            }
        }


        findViewById<Switch>(R.id.setting__syncLyricsSwitch).isChecked =
            !mainPrefInstance.getBoolean("isGoogle", true)

        findViewById<Switch>(R.id.setting__syncLyricsSwitch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    this,
                    "Synced Lyrics Turned on",
                    Toast.LENGTH_SHORT
                ).show()
                sharedEditor.apply {
                    putBoolean("isGoogle", false)
                }.apply()
            } else {
                Toast.makeText(
                    this,
                    "Synced Lyrics Turned Off",
                    Toast.LENGTH_SHORT
                ).show()
                sharedEditor.apply {
                    putBoolean("isGoogle", true)
                }.apply()
            }
        }

        findViewById<ImageView>(R.id.settingBackButton).setOnClickListener {
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

        findViewById<LinearLayout>(R.id.setting__clearHistory).setOnClickListener {
            Toast.makeText(
                this,
                "Cleared ${RecentlyPlayedDatabaseHandler(this).getHistorySize()} songs",
                Toast.LENGTH_SHORT
            ).show()
            RecentlyPlayedDatabaseHandler(this).clearHistoryDatabase()
        }

        findViewById<LinearLayout>(R.id.setting__clearSavedSongs).setOnClickListener {
            Toast.makeText(
                this,
                "Cleared ${BookmarkDatabaseHandler(this).readLyrics().size} songs",
                Toast.LENGTH_SHORT
            ).show()
            BookmarkDatabaseHandler(this).clearLyricsDatabase()
        }

        findViewById<LinearLayout>(R.id.setting__myName).setOnClickListener {
            RickRollcount++
            if (RickRollcount >= 7) {
                MyName(this)
                RickRollcount = 0
            }
        }

        findViewById<LinearLayout>(R.id.setting__coffee).setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/absanthosh"))
            ContextCompat.startActivity(this, browserIntent, null)
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