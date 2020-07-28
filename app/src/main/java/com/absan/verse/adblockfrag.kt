package com.absan.verse

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import com.absan.verse.R
import kotlinx.android.synthetic.main.notificationblockfrag.*


class adblockfrag : DialogFragment() {
    @SuppressLint("CommitPrefEdits")
    companion object {
        var ins: adblockfrag? = null
        fun getInstance(): adblockfrag? {
            return ins
        }
    }

    class ActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action")
            if (action == "action1") {
                performAction1()
            } else if (action == "action2") {
                performAction2()
            }
            //This is used to close the notification tray
            //val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            //context.sendBroadcast(it)
        }

        fun performAction1() {
            println("hello")
        adblockfrag.getInstance()?.notifistop()
        }
        fun performAction2() {println("world")}
    }

    override fun onStart() {
        var spotadblock = adblocktoggle
        TooltipCompat.setTooltipText(textView2, "Verse reads your notification to find spotify and mutes your phone when an Ad is playing.Simple.")
        val sharedPrefs = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)
        spotadblock.isChecked = sharedPrefs!!.getBoolean("value", false)

        notifiaccess.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        if (spotadblock.isChecked) {
            showNotification()
        } else {
            canclenotification()
        }

        spotadblock.setOnClickListener {
/*
            val sharedPrefs = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)
            if (!sharedPrefs!!.getBoolean("value", true)) {
                Toast.makeText(activity, "Turn off and on the notification access for verse to make ad block work.", Toast.LENGTH_LONG).show()
                Handler().postDelayed({ startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) },3000)
            } else {}
*/

            if (Settings.Secure.getString(activity?.contentResolver, "enabled_notification_listeners").contains("com.absan.verse"))
            {
                //service is enabled do something
                if (spotadblock.isChecked) {
                    val editor = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)?.edit()
                    editor!!.putBoolean("value", true)
                    editor.apply()
                    showNotification()
                } else {
                    val editor = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)?.edit()
                    editor?.putBoolean("value", false)
                    editor!!.apply()
                    canclenotification()
                }
            }
            else
            {
                println(Settings.Secure.getString(activity?.contentResolver, "enabled_notification_listeners"))
                Toast.makeText(activity, "Allow notification access for Verse on the upcoming settings", Toast.LENGTH_LONG).show()
                //service is not enabled try to enabled by calling...
                Handler().postDelayed({ startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) },3100)
            }
        }
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.notificationblockfrag, container, false)
    }

    fun notifistop() {
        val editor = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)?.edit()
        editor!!.putBoolean("value", false)
        editor.commit()
        val sharedPrefs = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)
        adblocktoggle.isChecked = sharedPrefs!!.getBoolean("value", false)
    }

    fun notifistart() {
        val editor = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)?.edit()
        editor!!.putBoolean("value", true)
        editor.commit()
        showNotification()
    }

    fun canclenotification() {
        val notificationManager =
            activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }

    fun showNotification() {
        val pi = PendingIntent.getActivity(activity, 0, Intent(activity, MainActivity::class.java), FLAG_UPDATE_CURRENT)

        //Stop blocking
        val intentAction = Intent(context, ActionReceiver::class.java)
        intentAction.putExtra("action","action1");
        val stopblocking:PendingIntent = getBroadcast(context,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT)

        var text = ""
        val sharedPrefs = activity?.getSharedPreferences("save", Context.MODE_PRIVATE)

        if (sharedPrefs!!.getBoolean("value", true)) {
            text = "Verse is blocking Spotify Ads"
        } else {
            text = "Verse is not blocking Spotify Ads"
        }

        val mNotificationManager =
            activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println(Build.VERSION.SDK_INT.toString() + "  " + Build.VERSION_CODES.O.toString())
            val channel = NotificationChannel(
                "VerseId",
                "VerseChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(activity!!, "VerseId")
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle("Verse")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Turn Off blocking settings to remove notification"))
            .setOngoing(true)
            .setContentIntent(pi)
            //.addAction(android.R.mipmap.sym_def_app_icon, "Stop blocking", stopblocking)
            .build()

        mNotificationManager.notify(0, notification)
    }

}
