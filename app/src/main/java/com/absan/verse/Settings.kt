package com.absan.verse

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.Utils.*
import com.aigestudio.wheelpicker.WheelPicker
import com.google.android.material.bottomsheet.BottomSheetDialog


class Settings : AppCompatActivity() {
    private var RickRollcount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.settings)
        super.onCreate(savedInstanceState)

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

            fontRoller?.data = fontList

//            Log.e(
//                "Roller",
//                fontList.get
//            )


            fontRoller?.setOnItemSelectedListener { fontRoller, fontList, position ->
                run {

                    val fontListData =
                        resources.getStringArray(R.array.family_names).toMutableList()
                    Log.e("Roller", fontListData.get(position))

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