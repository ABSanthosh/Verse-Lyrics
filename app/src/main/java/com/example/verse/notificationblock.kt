package com.example.verse

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.Builder
import kotlinx.android.synthetic.main.activity_notificationblock.*


class ActionReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action")
        if (action == "stop") {
            notificationblock.getInstance()?.notifistop()
        }

        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(it)
    }

}


@Suppress("DEPRECATION")
class notificationblock : AppCompatActivity() {

    companion object {
        var ins: notificationblock? = null
        fun getInstance(): notificationblock? {
            return ins
        }
    }

    lateinit var sharedPreferences: SharedPreferences
    lateinit var NotificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.verse.R.layout.activity_notificationblock)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        ins = this

        var back_arrow: ImageView = findViewById(com.example.verse.R.id.backarrow)

        back_arrow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val sharedPrefs = getSharedPreferences("save", Context.MODE_PRIVATE)
        adblocker.setChecked(sharedPrefs.getBoolean("value", true))

        if (sharedPrefs.getBoolean("value", true)) {
            textView3.setText("Blocking")
        } else {
            textView3.setText("Not Blocking")
        }


        adblocker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (adblocker.isChecked()) {
                    val editor = getSharedPreferences("save", Context.MODE_PRIVATE).edit()
                    editor.putBoolean("value", true)
                    editor.commit()
                    textView3.setText("Blocking")
                    showNotification()
                } else {
                    val editor = getSharedPreferences("save", Context.MODE_PRIVATE).edit()
                    editor.putBoolean("value", false)
                    editor.commit()
                    textView3.setText("Not Blocking")
                    showNotification()
                }
            }
        })
    }

    fun notifistop() {
        val editor = getSharedPreferences("save", Context.MODE_PRIVATE).edit()
        editor.putBoolean("value", false)
        editor.commit()
        textView3.setText("Not Blocking")
        showNotification()
        adblocker.setChecked(false)
    }

    fun notifistart() {
        val editor = getSharedPreferences("save", Context.MODE_PRIVATE).edit()
        editor.putBoolean("value", true)
        editor.commit()
        textView3.setText("Blocking")
        showNotification()
        adblocker.setChecked(true)
    }


    fun showNotification() {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, notificationblock::class.java), 0)

        //Stop blocking

        val intentAction = Intent(this, adblockfrag.ActionReceiver::class.java)
        intentAction.putExtra("action", "stop")
        val stopitisay = PendingIntent.getBroadcast(this,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);


        var text = ""
        val sharedPrefs = getSharedPreferences("save", Context.MODE_PRIVATE)
        if (sharedPrefs.getBoolean("value", true)){
            text = "Verse is blocking Spotify Ads"
        }else{
            text = "Verse is not blocking Spotify Ads"
        }

        val r: Resources = resources
        val notification: Notification = Builder(this)
            .setSmallIcon(R.drawable.ic_menu_report_image)
            .setContentTitle("Verse")
            .setContentText(text)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .addAction(R.mipmap.sym_def_app_icon, "Stop blocking", stopitisay)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}