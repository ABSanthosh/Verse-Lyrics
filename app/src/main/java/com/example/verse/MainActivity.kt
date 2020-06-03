package com.example.verse

import android.app.AlertDialog
import android.app.Notification
import android.app.Service
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import java.util.*
import kotlin.system.exitProcess


var stoprecalling = ""
public var songavailable: Boolean = true
public var song: String = ""
public var updatesongname: String = ""
public var firsttime: Boolean = true

class CustomNotificationListene : NotificationListenerService() {
    private var muted = false
    private var originalVolume = 0
    private var zeroVolume = 0
    private var blocklist = listOf<String>("Advertisement","spotify")


    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
/*
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this@CustomNotificationListener, "default")
        mBuilder.setContentTitle("My Notification")
        mBuilder.setContentText("Notification Listener Service Example")
        mBuilder.setTicker("Notification Listener Service Example")
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        mBuilder.setAutoCancel(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("10001", "NOTIFICATION_CHANNEL_NAME", importance)
            mBuilder.setChannelId("10001")
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())

*/
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startID: Int): Int {
        timer = Timer()
        isRunning = true
        muted = false
        originalVolume = 0
        zeroVolume = 0



        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val notifications = activeNotifications
                var notification = Notification()
                var foundNotification = false
                if (notifications != null) {
                    // Find which notification is Spotify
                    for (i in notifications.indices) {
                        val name = notifications[i].packageName
                        if (name.contains("spotify")) {
                            notification = notifications[i].notification
                            foundNotification = true
                            break
                        }
                    }
                    // Check if it is an ad
                    if (foundNotification) {
                        val extras = notification.extras
                        val title =
                            extras.getCharSequence(Notification.EXTRA_TITLE).toString()
                        if (title != null) {
                            val isAdPlaying = blocklist!!.contains(title)
                            val s =
                                if (isAdPlaying) "Ad playing" else "Ad not playing"
                            Log.i(null,isAdPlaying.toString())
                            val audioManager =
                                getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            if (isAdPlaying && !muted) {
                                originalVolume =
                                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    zeroVolume,
                                    AudioManager.FLAG_SHOW_UI

                                )
                                MainActivity.getInstance()?.updateTheTextView("Advertisement")
                                muted = true
                            } else if (!isAdPlaying && muted) {
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    originalVolume,
                                    AudioManager.FLAG_SHOW_UI
                                )
                                muted = false
                            }
                        }
                    }
                }
            }
        }, 10, 250)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        try {
            killService()
        } catch (ex: NullPointerException) {
        }
    }

    override fun onNotificationPosted(notification: StatusBarNotification) {}
    override fun onNotificationRemoved(notification: StatusBarNotification) {}

    companion object {
        private var timer: Timer? = null
        var isRunning = false
            private set

        fun killService() {
            timer?.cancel()
            isRunning = false
        }

    }

}

class SpotifyBroadcastReceiver : BroadcastReceiver() {
    internal object BroadcastTypes {
        val SPOTIFY_PACKAGE = "com.spotify.music"
        val PLAYBACK_STATE_CHANGED = "$SPOTIFY_PACKAGE.playbackstatechanged"
        val QUEUE_CHANGED = "$SPOTIFY_PACKAGE.queuechanged"
        val METADATA_CHANGED = "$SPOTIFY_PACKAGE.metadatachanged"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val timeSentInMs = intent.getLongExtra("timeSent", 0L)
        val action = intent.getAction()
        var a = Thread {
            if (action == BroadcastTypes.METADATA_CHANGED) {
                val trackId = intent.getStringExtra("id")
                val artistName = intent.getStringExtra("artist")
                val albumName = intent.getStringExtra("album")
                val trackName = intent.getStringExtra("track")
                var song_name: String = ""
                //Log.d("DEBUG", trackName)

                song_name = trackName.split("(")[0]
                song_name = trackName.split("-")[0]
                updatesongname = song_name

                var sn = song_name.toString()
                var an = artistName.toString()

                if (an[an.length - 1] == ' ') {
                    an = an.dropLast(1)
                    an = an.replace(" ", "+")
                } else {
                    an = an.replace(" ", "+")
                }

                if (sn[sn.length - 1] == ' ') {
                    sn = sn.dropLast(1)
                    sn = sn.replace(" ", "+")
                } else {
                    sn = sn.replace(" ", "+")
                }

                song = sn + "+" + an

                MainActivity.getInstance()?.shouldParseHTML(song)
                MainActivity.getInstance()?.updateTheTextView(updatesongname.toString())
                MainActivity.getInstance()?.statuschecker()
            }
        }
        if (a.isAlive) {
            a.run()
        } else {
            a.start()
        }
    }


}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var ins: MainActivity? = null
        fun getInstance(): MainActivity? {
            return ins
        }

    }


    lateinit var mreceiver: SpotifyBroadcastReceiver
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
         )
        sharedPreferences = getSharedPreferences("broadcastnotification", Context.MODE_PRIVATE)
        firsttime = sharedPreferences.getBoolean("firsttime", true)
        println((NotificationManagerCompat.from(this).areNotificationsEnabled()).toString()+"---------------------")
        var handler = Handler(Looper.getMainLooper())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
///////////////////Only first time/////////////////////////
        if (firsttime) {
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            firsttime = false
            editor.putBoolean("firsttime", firsttime)
            editor.apply()
            enablebroadcast()

        }
//////////////////////////////////////////////////////////

        ins = this

        NotificationManagerCompat.from(this).areNotificationsEnabled()

        var drawerlayout: DrawerLayout = findViewById(R.id.drawer_layout)
        var navigationview: NavigationView = findViewById(R.id.nav_view)
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        navigationview.bringToFront()
        mreceiver = SpotifyBroadcastReceiver()

        var toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        navigationview.setNavigationItemSelectedListener(this)

        var serviceIntent = Intent(this, CustomNotificationListene::class.java)
        startService(serviceIntent)
        //startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        var logo: TextView = findViewById(R.id.logo)
        logo.visibility = View.VISIBLE
        //status.visibility = View.VISIBLE
        toggle.isDrawerIndicatorEnabled = true

        //val s: TextView = findViewById(R.id.status)
        //s.setOnClickListener { openspotify() }
        //s.bringToFront()

        if (!isTaskRoot()
            && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
            && getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_MAIN)
        ) {

            finish();
            return;
        }

    }


    fun statuschecker() {
        status.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        var intentfilter1: IntentFilter = IntentFilter("com.spotify.music.playbackstatechanged")
        var intentfilter2: IntentFilter = IntentFilter("com.spotify.music.metadatachanged")
        var intentfilter3: IntentFilter = IntentFilter("com.spotify.music.queuechanged")

        intentfilter1.addCategory(Intent.CATEGORY_DEFAULT)
        intentfilter2.addCategory(Intent.CATEGORY_DEFAULT)
        intentfilter3.addCategory(Intent.CATEGORY_DEFAULT)

        registerReceiver(mreceiver, intentfilter1)
        registerReceiver(mreceiver, intentfilter2)
        registerReceiver(mreceiver, intentfilter3)

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mreceiver)

    }

    // TODO: 30-05-2020 FIX MUSIXMATCH SONG SEARCHER
    /* fun try_musixmatch(songname: String) {
         var id_link = songname
         id_link = "www.musixmatch.com/lyrics"
         Thread() {
             var ua =
                 "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
             Jsoup.connect(id_link).userAgent(ua).get().run {
                 select("body").forEachIndexed { index, element ->
                     var el = element.toString()
                     println("-------------------------" + el)
                 }
             }
         }.start()
     }
 */

    fun try_google(songname: String) {
        runOnUiThread { lyrics.setText("") }
        lyrics.scrollTo(0, 0)
        var line_count = 0
        var lyrics_appended = false
        var id_link = songname
        id_link = "https://www.google.com/search?q=" + id_link
        println(id_link)
        var ly_thread: Thread = Thread {
            try {
                var ua =
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
                Jsoup.connect(id_link).userAgent(ua).get().run {
                    select("[jsname='YS01Ge']").forEachIndexed { index, element ->
                        var el = element.toString()
                        var line = el.replace(("<span jsname=" + '"' + "YS01Ge" + '"' + ">"), "")
                        line = line.replace("</span>", "")
                        runOnUiThread(fun() {
                            if (line_count > 3) {
                                lyrics.append(line)
                                lyrics.append("\n")
                                lyrics.append("\n")
                                lyrics_appended = true
                            } else {
                                line_count++
                            }
                        })
                    }
                }

            } catch (e: Exception) {
            }
        }
        if (ly_thread.isAlive) {
            ly_thread.run()
        } else {
            ly_thread.start()
        }

    }

    fun enablebroadcast() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Broadcast Settings")
        builder.setMessage("Turn on broadcast option in spotify to get lyrics.\n\nSpotify >Settings >Device Broadcast Status\n\nIs the app not working even after turning on Device Broadcast status in spotify?\nSend me a feedback")
        builder.setPositiveButton("Open Spotify", { dialog, which -> openspotify() })
        builder.setNeutralButton("Feedback", { dialog, which -> sendfeedback() })
        builder.setNegativeButton("Close Verse", { dialog, which -> dialog.cancel() })
        builder.show()
    }

    fun updateTheTextView(t: String) {
        this@MainActivity.runOnUiThread {
            songname.visibility = View.VISIBLE
            lyrics.visibility = View.VISIBLE
            lyrics.movementMethod = ScrollingMovementMethod()
            status.visibility = View.INVISIBLE
            //lyrics.bringToFront()
            songname.setText(t)
        }

    }

    fun sendfeedback() {
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf("a.b.santhosh02@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Verse app")
        intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        startActivity(Intent.createChooser(intent, "Send mail"))
    }

    fun openspotify() {
        try {
            val sendIntent = packageManager.getLaunchIntentForPackage("com.spotify.music")
            startActivity(sendIntent)
            finishAffinity()
        } catch (e: java.lang.Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
                )
            )
        }
        Toast.makeText(this, "Open verse again", Toast.LENGTH_LONG).show()
        exitProcess(0)
    }

    fun shouldParseHTML(idlink: String) {
        if (idlink == "ad") {
            runOnUiThread(
                fun() {
                    lyrics.setText("Please stand by")
                }
            )
        } else {
            var sn = idlink
            if (stoprecalling != sn) {
                stoprecalling = sn

                try {
                    try_google(sn)
                } catch (e: java.lang.Exception) {
                    lyrics.setText("")
                    lyrics.setText("Please try different song. /nUnable to request lyrics for the given song")

                }
            }
        }
    }

    fun opengithub() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ABSanthosh"))
        startActivity(browserIntent)
    }

    fun versehelp(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Help")
        builder.setMessage("1) Verse keep shows 'play a song on spotify?'\n\nIt's posible that you haven't turned on Device Broadcast status on the first usage of Verse\n\nSpotify >Settings >Device Broadcast Status\n\nIts possible that theres a bug in app.\n\nSend me feedback with the steps to reproduce the error that you encountered.\n\n\n2)Only song name is displayed and not the lyrics?\n\nThis app does not have the ability to give lyrics for all the songs(sometimes even if its quiet popular)\n\nAlso lyrics for remixes won't match because...you know its called a remix for a reason")
        builder.setPositiveButton("Ok", { dialog, which -> onBackPressed()})
        builder.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.app_about1 -> {
                Toast.makeText(this, "   Thanks   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about2 -> {
                Toast.makeText(this, "   For   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about3 -> {
                Toast.makeText(this, "   Trying   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about4 -> {
                Toast.makeText(this, "   Verse   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about5 -> {
                Toast.makeText(this, "   ❤❤❤K   ", Toast.LENGTH_SHORT).show()
            }
            R.id.otherapps -> {
                opengithub()
            }
            R.id.open_spotify -> {
                openspotify()
            }
            R.id.helper -> {
                versehelp()
            }
            R.id.feedback -> sendfeedback()
            R.id.myname -> {
                Toast.makeText(this, "   RIP IOS USERS   ", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }


}

/*
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var ins: MainActivity? = null
        fun getInstance(): MainActivity? {
            return ins
        }
    }

    lateinit var mreceiver: SpotifyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lateinit var logomovement: Animation
        ins = this

        NotificationManagerCompat.from(this).areNotificationsEnabled()
        logomovement = AnimationUtils.loadAnimation(this, R.anim.logomovement)

        var drawerlayout: DrawerLayout = findViewById(R.id.drawer_layout)
        var navigationview: NavigationView = findViewById(R.id.nav_view)
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        navigationview.bringToFront()
        mreceiver = SpotifyBroadcastReceiver()

        var toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        navigationview.setNavigationItemSelectedListener(this)

        //var serviceIntent = Intent(this, CustomNotificationListener::class.java)
        //startService(serviceIntent)
        //startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        Handler().postDelayed({
            val logo: TextView = findViewById(R.id.logo)
            logo.startAnimation(logomovement)

            Handler().postDelayed({ logo.gravity = Gravity.TOP }, 1500)
            Handler().postDelayed({
                status.visibility = View.VISIBLE
                toggle.isDrawerIndicatorEnabled = true
            }, 1750)

        }, 2000)

        //val s: TextView = findViewById(R.id.status)
        //s.setOnClickListener { openspotify() }
        //s.bringToFront()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        var intentfilter1: IntentFilter = IntentFilter("com.spotify.music.playbackstatechanged")
        var intentfilter2: IntentFilter = IntentFilter("com.spotify.music.metadatachanged")
        var intentfilter3: IntentFilter = IntentFilter("com.spotify.music.queuechanged")

        intentfilter1.addCategory(Intent.CATEGORY_DEFAULT)
        intentfilter2.addCategory(Intent.CATEGORY_DEFAULT)
        intentfilter3.addCategory(Intent.CATEGORY_DEFAULT)

        registerReceiver(mreceiver, intentfilter1)
        registerReceiver(mreceiver, intentfilter2)
        registerReceiver(mreceiver, intentfilter3)

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mreceiver)
    }

    // TODO: 30-05-2020 FIX MUSIXMATCH SONG SEARCHER
    /* fun try_musixmatch(songname: String) {
         var id_link = songname
         id_link = "www.musixmatch.com/lyrics"
         Thread() {
             var ua =
                 "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
             Jsoup.connect(id_link).userAgent(ua).get().run {
                 select("body").forEachIndexed { index, element ->
                     var el = element.toString()
                     println("-------------------------" + el)
                 }
             }
         }.start()
     }
 */

    fun try_google(songname: String) {
        logo.gravity = Gravity.TOP
        lyrics.setText("")
        lyrics.scrollTo(0, 0)
        var line_count = 0
        var lyrics_appended = false
        var id_link = songname
        id_link = "https://www.google.com/search?q=" + id_link
        println(id_link)
        var ly_thread: Thread = Thread {
            try {
                var ua =
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
                Jsoup.connect(id_link).userAgent(ua).get().run {
                    select("[jsname='YS01Ge']").forEachIndexed { index, element ->
                        var el = element.toString()
                        var line = el.replace(("<span jsname=" + '"' + "YS01Ge" + '"' + ">"), "")
                        line = line.replace("</span>", "")

                        if (line_count > 3) {
                            runOnUiThread(fun() {
                                lyrics.append(line)
                                lyrics.append("\n")
                                lyrics.append("\n")
                            })

                            lyrics_appended = true
                        } else {
                            line_count++
                        }
                    }
                }

            } catch (e: Exception) {
                println("--------------------------" + e)
            }
        }
        if (ly_thread.isAlive) {
            ly_thread.run()
        } else {
            ly_thread.start()
        }

    }

    fun updateTheTextView(t: String) {
        synchronized(logo.gravity != Gravity.TOP) {
            this@MainActivity.runOnUiThread {
                Handler().postDelayed({
                    songname.visibility = View.VISIBLE
                    lyrics.visibility = View.VISIBLE
                    lyrics.movementMethod = ScrollingMovementMethod()
                    status.visibility = View.INVISIBLE
                    //lyrics.bringToFront()
                }, 1500)
                songname.setText(t)
            }
        }
    }

    fun sendfeedback() {
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf("a.b.santhosh02@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Verse app")
        intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        startActivity(Intent.createChooser(intent, "Send mail"))
    }

    fun openspotify() {
        try {
            val sendIntent = packageManager.getLaunchIntentForPackage("com.spotify.music")
            startActivity(sendIntent)
            finishAffinity()
        } catch (e: java.lang.Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
                )
            )
        }
    }

    fun shouldParseHTML(idlink: String) {
        if (idlink == "ad") {
            runOnUiThread(
                fun() {
                    lyrics.setText("Please stand by")
                }
            )
        } else {
            var sn = idlink
            if (stoprecalling != sn) {
                stoprecalling = sn

                try {
                    try_google(sn)
                } catch (e: java.lang.Exception) {
                    lyrics.setText("")
                    println("-----------------" + e)
                    lyrics.setText("Please try different song. /nUnable to request lyrics for the given song")

                }


            }
        }
    }

    fun opengithub() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ABSanthosh"))
        startActivity(browserIntent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.app_about1 -> {
                Toast.makeText(this, "   Thanks   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about2 -> {
                Toast.makeText(this, "   For   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about3 -> {
                Toast.makeText(this, "   Trying   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about4 -> {
                Toast.makeText(this, "   Verse   ", Toast.LENGTH_SHORT).show()
            }
            R.id.app_about5 -> {
                Toast.makeText(this, "   ❤❤❤K   ", Toast.LENGTH_SHORT).show()
            }
            R.id.otherapps -> {
                opengithub()
            }
            R.id.open_spotify -> {
                openspotify()
            }
            R.id.feedback -> sendfeedback()
            R.id.myname -> {
                Toast.makeText(this, "   RIP IOS USERS   ", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }


}



 */