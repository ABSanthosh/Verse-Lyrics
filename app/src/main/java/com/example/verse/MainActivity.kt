package com.example.verse

import android.app.Notification
import android.app.Service
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.method.ScrollingMovementMethod
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
import org.jsoup.select.Elements
import java.net.URLEncoder
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess


var stoprecalling = ""
public var songavailable: Boolean = true
public var song: String = ""
public var s: String = ""
public var updatesongname: String = ""
public var fuckingad: Boolean = false
public var firsttime: Boolean = true
var blockad by Delegates.notNull<Boolean>()


class CustomNotificationListener : NotificationListenerService() {
    private var muted = false
    private var originalVolume = 0
    private var zeroVolume = 0
    private var blocklist = listOf<String>("Advertisement", "Spotify")
    lateinit var sharedPreferences: SharedPreferences

    companion object {
        private var timer: Timer? = null
        var isRunning = false
            private set

        fun killService() {
            timer?.cancel()
            isRunning = false
        }

        var ins: CustomNotificationListener? = null
        fun getInstance(): CustomNotificationListener? {
            return ins
        }
    }


    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        ins = this
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
                sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor = sharedPreferences.edit()

                var data = sharedPreferences.getBoolean("value", false)
                fuckingad = sharedPreferences.getBoolean("adstatus", false)
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
                            s = if (isAdPlaying) "Ad" else "nAd"
                            if (isAdPlaying) {
                                editor.putBoolean("adstatus", true)
                                editor.commit()
                            } else {
                                editor.putBoolean("adstatus", false)
                                editor.commit()
                            }
                            val audioManager =
                                getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            if (isAdPlaying && !muted && data) {
                                originalVolume =
                                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    zeroVolume,
                                    AudioManager.FLAG_SHOW_UI
                                )
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


    override fun onNotificationPosted(notification: StatusBarNotification) {
        super.onNotificationPosted(notification)
    }

    override fun onNotificationRemoved(notification: StatusBarNotification) {
        super.onNotificationRemoved(notification)
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

                MainActivity.getInstance()?.shouldParseHTML(URLEncoder.encode(updatesongname,"utf-8"))
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
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)

        sp = getSharedPreferences("theme", Context.MODE_PRIVATE)

        if (sp.getBoolean("light", true)) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        setContentView(R.layout.activity_main)


        sharedPreferences = getSharedPreferences("broadcastnotification", Context.MODE_PRIVATE)
        val sharedPrefs = getSharedPreferences("save", Context.MODE_PRIVATE)

        if (sharedPrefs.getBoolean("value", true)) {
            adblockfrag.getInstance()?.showNotification()
        } else {
            try {
                adblockfrag.getInstance()?.canclenotification()
            } catch (e: java.lang.Exception) {
            }
        }

        firsttime = sharedPreferences.getBoolean("firsttime", true)
        var handler = Handler(Looper.getMainLooper())

        if (firsttime) {
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            firsttime = false
            editor.putBoolean("firsttime", firsttime)
            editor.apply()
            enablebroadcast()

        }

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

        var serviceIntent = Intent(this, CustomNotificationListener::class.java)
        startService(serviceIntent)
        //startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))


        var logo: TextView = findViewById(R.id.logo)
        logo.visibility = View.VISIBLE
        toggle.isDrawerIndicatorEnabled = true

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
        runOnUiThread {
            status.visibility = View.INVISIBLE
        }
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

        sp = getSharedPreferences("theme", Context.MODE_PRIVATE)

        if (sp.getBoolean("light", true)) {
            idk(true)
        } else {
            idk(false)
        }
        stoprecalling = ""

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
        //println(id_link)
        var ly_thread: Thread = Thread {
            try {
                var ua =
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
                Jsoup.connect(id_link).userAgent(ua).get().run {
                    val elements: Elements = select("[jsname='YS01Ge']")
                    if (elements.size != 0) {
                        select("[jsname='YS01Ge']").forEachIndexed { index, element ->
                            var el = element.toString()
                            var line =
                                el.replace(("<span jsname=" + '"' + "YS01Ge" + '"' + ">"), "")
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
                    }else{
                        runOnUiThread { lyrics.setText("Lyrics not available for this song :( ") }
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
        var eb: firsttimemsg = firsttimemsg()
        eb.show(supportFragmentManager, "FirstTimefragment")

    }

    fun updateTheTextView(t: String) {
        this@MainActivity.runOnUiThread {
            songname.visibility = View.VISIBLE
            lyrics.visibility = View.VISIBLE
            lyrics.movementMethod = ScrollingMovementMethod()
            status.visibility = View.INVISIBLE
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
        var sn = idlink

        //Deprecated (Used to check if the lyrics are being append more than once but has clashed with recreate so removed and surprisingly it only appends once)
        //sike you thought! It fuking again did the same. so the variable stoprecalling is set to "" onstart so no problem.
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

    fun opengithub() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ABSanthosh"))
        startActivity(browserIntent)
    }

    fun versehelp() {
        var df: helpfragment = helpfragment()
        df.show(supportFragmentManager, "Helpfragment")
    }

    fun openadblock() {
        var adb: adblockfrag = adblockfrag()
        adb.show(supportFragmentManager, "ADBlockfrag")
    }

    fun aboutapp() {
        var af: aboutfrag = aboutfrag()
        af.show(supportFragmentManager, "Aboutfragment")

    }

    fun opennbactivity() {
        var Intent = Intent(this@MainActivity, notificationblock::class.java)
        startActivity(Intent)
    }

    fun adblockfunction(state: Boolean) {
        blockad = state
        Toast.makeText(this, state.toString(), Toast.LENGTH_SHORT).show()
    }


    fun idk(t: Boolean) {

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val menu = navigationView.menu

        //Item declaration/referrence
        val theme = menu.findItem(R.id.mode)

        if (t == false) {
            //if darkmode
            theme.title = getString(R.string.light)
            theme.icon = getDrawable(R.drawable.lightmode)
        } else {
            //if lightmode
            theme.title = getString(R.string.dark)
            theme.icon = getDrawable(R.drawable.colormode)
        }
    }

    fun changetheme() {
        var editor: SharedPreferences.Editor = sp.edit()
        var e = sp.getBoolean("light", true)
        if (e) {
            //Change to dark mode

            //setTheme(R.style.DarkTheme);
            //recreate()
            editor.putBoolean("light", false)
            editor.apply()
            recreate()

        } else {
            //Change to light mode

            //setTheme(R.style.LightTheme);

            editor.putBoolean("light", true)
            editor.apply()
            recreate()

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.otherapps -> {
                opengithub()
            }
            R.id.open_spotify -> {
                openspotify()
            }
            R.id.helper -> {
                versehelp()
            }
            R.id.adblockmenu -> {
                //opennbactivity()
                openadblock()
            }
            R.id.aboutapp -> aboutapp()
            R.id.feedback -> sendfeedback()
            R.id.mode -> {
                changetheme()
            }

            R.id.myname -> {
                Toast.makeText(this, "   RIP IOS USERS   ", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }


}
