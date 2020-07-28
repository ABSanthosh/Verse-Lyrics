package com.absan.verse

import android.Manifest
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess


var stoprecalling = ""
var stoprecalling2 = ""
public var songavailable: Boolean = true
public var song: String = ""
public var s: String = ""
public var updatesongname: String = ""
public var fuckingad: Boolean = false
public var firsttime: Boolean = true
var blockad by Delegates.notNull<Boolean>()


//Service used to check if spotify is playing ad or song
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

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
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
                try {
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
                } catch (e: java.lang.Exception) {
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

//Used to get data from spotify to know what song is being played
//Copied from a spotify's dev page
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

                MainActivity.getInstance()?.shouldParseHTML(updatesongname, artistName)
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



class MainActivity : AppCompatActivity(), IACRCloudListener,
    NavigationView.OnNavigationItemSelectedListener, LifecycleObserver {

    //floating window init
    lateinit var songid:String
    //Variables for song detection

    private val TAG = "MainActivity"

    private var mProcessing = false
    private var mAutoRecognizing = false
    private var initState = false

    private var path = ""

    private var startTime: Long = 0
    private val stopTime: Long = 0

    private val PRINT_MSG = 1001

    private var mConfig: ACRCloudConfig = ACRCloudConfig()
    private var mClient: ACRCloudClient = ACRCloudClient()

    private var mResult: TextView? = null

    //Song detection variables end here

    companion object {
        var ins: MainActivity? = null
        fun getInstance(): MainActivity? {
            return ins
        }
    }

    lateinit var mreceiver: SpotifyBroadcastReceiver
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sp: SharedPreferences
    lateinit var default_theme: SharedPreferences
    lateinit var nooflines: String
    private var currentToast: Toast? = null
    var activityVisible by Delegates.notNull<Boolean>()
    var f by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        sp = getSharedPreferences("theme", Context.MODE_PRIVATE)

        if (sp.getString("light", "true") == "T") {
            setTheme(R.style.LightTheme);
        } else if (sp.getString("light", "true") == "F") {
            setTheme(R.style.DarkTheme);
        } else {
            if (isDarkThemeOn()) {
                setTheme(R.style.DarkTheme);
            } else {
                setTheme(R.style.LightTheme);
            }
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

            sharedPreferences.edit().clear()
            sp.edit().clear()

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
        //Below All for music recogonisation

        mResult = findViewById<TextView>(R.id.acrlyrics)



        this.mConfig.acrcloudListener = this
        this.mConfig.context = this

        this.mConfig.host = "identify-global.acrcloud.com"
        this.mConfig.accessKey = "->                                <-" //Access key goes here
        this.mConfig.accessSecret = "->                                   <-"//Access Secret goes here

        // If you do not need volume callback, you set it false.
        this.mConfig.recorderConfig.isVolumeCallback = false

        this.mClient = ACRCloudClient()
        this.initState = this.mClient.initWithConfig(this.mConfig)

        songll.bringToFront()
        startid2.bringToFront()
        startid2.setOnClickListener { rec() }
        pulsator.bringToFront()
        startid.bringToFront()
        startid.setOnClickListener { rec() }

    }

    fun isActivityVisible(): Boolean {
        return activityVisible
    }

    override fun onResume() {
        super.onResume()
        activityVisible = true
    }

    override fun onPause() {
        super.onPause()
        activityVisible = false
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

        if (sp.getString("light", "T") == "T") {
            idk("T")
        } else if (sp.getString("light", "T") == "F") {
            idk("F")
        } else {
            idk("N")
        }
        stoprecalling = ""
        stoprecalling2 = ""

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

    fun try_google(songname: String, an: String) {
        runOnUiThread { lyrics.setText("") }
        lyrics.scrollTo(0, 0)
        var line_count = 0
        var lyrics_appended = false
        var id_link = songname
        id_link = "https://www.google.com/search?q=" + URLEncoder.encode(
            id_link,
            "utf-8"
        ) + "+" + URLEncoder.encode(an, "utf-8") + "+" + "lyrics"
        var ly_thread: Thread = Thread {
            try {
                var ua =
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
                Jsoup.connect(id_link).userAgent(ua).get().run {
                    val elements: Elements = select("[jsname='YS01Ge']")
                    //println(elements)
                    if (elements.size != 0) {
                        select("[jsname='YS01Ge']").forEachIndexed { index, element ->
                            var el = element.toString()
                            var line =
                                el.replace(("<span jsname=" + '"' + "YS01Ge" + '"' + ">"), "")
                            line = line.replace("</span>", "")
                            runOnUiThread(fun() {
                                if (line_count >= 0) {
                                    lyrics.append(line)
                                    lyrics.append("\n")
                                    lyrics.append("\n")
                                    lyrics_appended = true
                                } else {
                                    line_count++
                                }
                            })
                        }
                    } else {
                        runOnUiThread {
                            lyrics.setText(
                                "Lyrics not available for \n [" + URLDecoder.decode(
                                    songname,
                                    "utf-8"
                                ) + "]\n :( "
                            )
                        }
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

    fun try_google_for_acr(songname: String, an: String) {
        runOnUiThread { acrlyrics.setText("") }
        acrlyrics.scrollTo(0, 0)
        var line_count = 0
        var lyrics_appended = false
        var id_link = songname
        id_link = "https://www.google.com/search?q=" + URLEncoder.encode(
            id_link,
            "utf-8"
        ) + "+" + URLEncoder.encode(an, "utf-8") + "+" + "lyrics"
        var ly_thread: Thread = Thread {
            try {
                var ua =
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
                Jsoup.connect(id_link).userAgent(ua).get().run {
                    val elements: Elements = select("[jsname='YS01Ge']")
                    //println(elements)
                    if (elements.size != 0) {
                        select("[jsname='YS01Ge']").forEachIndexed { index, element ->
                            var el = element.toString()
                            var line =
                                el.replace(("<span jsname=" + '"' + "YS01Ge" + '"' + ">"), "")
                            line = line.replace("</span>", "")
                            runOnUiThread(fun() {
                                if (line_count >= 0) {
                                    acrlyrics.append(line)
                                    acrlyrics.append("\n")
                                    acrlyrics.append("\n")
                                    lyrics_appended = true
                                } else {
                                    line_count++
                                }
                            })
                        }
                    } else {
                        runOnUiThread {
                            acrlyrics.setText(
                                "Lyrics not available for \n [" + URLDecoder.decode(
                                    songname,
                                    "utf-8"
                                ) + "]\n :( "
                            )
                        }
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
        if (viewflipper.displayedChild == 0) {
            this@MainActivity.runOnUiThread {
                songname.visibility = View.VISIBLE
                lyrics.visibility = View.VISIBLE
                lyrics.movementMethod = ScrollingMovementMethod()
                status.visibility = View.INVISIBLE
                songname.setText(t)
            }
        } else {
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

    fun shouldParseHTML(idlink: String, an: String) {
        var sn = idlink
        var an = an
        var utf_sn = URLEncoder.encode(sn, "utf-8")
        var utf_an = URLEncoder.encode(an, "utf-8")
        //Deprecated (Used to check if the lyrics are being append more than once but has clashed with recreate so removed and surprisingly it only appends once)
        //sike you thought! It fuking again did the same. so the variable stoprecalling is set to "" onstart so no problem.
        if (stoprecalling != sn) {
            stoprecalling = sn
            try {
                //try_syair(sn, an)
                //try_musixmatch(sn, an)
                try_google(sn, an)
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

    fun whatsnewfrag() {
        var wn: whatsnew_control = whatsnew_control()
        wn.show(supportFragmentManager, "Whatsnew")
    }

    fun adblockfunction(state: Boolean) {
        blockad = state
        Toast.makeText(this, state.toString(), Toast.LENGTH_SHORT).show()
    }

    fun idk(t: String) {

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val menu = navigationView.menu

        //Item declaration/referrence
        val theme = menu.findItem(R.id.mode)

        if (t == "F") {
            //if darkmode
            theme.title = "System default"
            if (isDarkThemeOn()) {
                theme.icon = getDrawable(R.drawable.colormode)
            } else {
                theme.icon = getDrawable(R.drawable.lightmode)
            }

        } else if (t == "T") {
            //if lightmode
            theme.title = getString(R.string.dark)
            theme.icon = getDrawable(R.drawable.colormode)
        } else {
            theme.title = getString(R.string.light)
            theme.icon = getDrawable(R.drawable.lightmode)
        }
    }

    fun changetheme() {
        var editor: SharedPreferences.Editor = sp.edit()
        var e = sp.getString("light", "T")
        if (e == "T") {
            //Change to dark mode
            editor.putString("light", "F")
            editor.apply()
            recreate()
        } else if (e == "F") {
            //Change to light mode
            editor.putString("light", "N")
            editor.apply()
            recreate()
        } else {
            //check system default
            editor.putString("light", "T")
            editor.apply()
            recreate()
        }
    }

    fun rec() {
        verifyPermissions()
        start()
        var pulsator: PulsatorLayout = findViewById(R.id.pulsator)
        startid.visibility = View.VISIBLE
        startid2.visibility = View.INVISIBLE
        spot.visibility = View.GONE
        pulsator.bringToFront()
        pulsator.start()
    }

    fun firstview() {
        viewflipper.displayedChild = 0
        startid.visibility = View.VISIBLE
        startid2.visibility = View.INVISIBLE
        pulsator.bringToFront()
        startid.bringToFront()
        startid.setOnClickListener { rec() }

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val menu = navigationView.menu
        val isong = menu.findItem(R.id.acr)

        songname.text = updatesongname

        isong.title = "Identify Song"
        isong.icon = getDrawable(R.drawable.ear)
    }

    fun secondview() {
        viewflipper.displayedChild = 1
        reset()
        updateTheTextView(updatesongname.toString())
        startid.visibility = View.VISIBLE
        startid2.visibility = View.INVISIBLE
        pulsator.bringToFront()
        startid.bringToFront()
        startid.setOnClickListener { rec() }

        if (status.visibility == View.VISIBLE) {
            status.visibility = View.INVISIBLE
        }

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val menu = navigationView.menu
        val isong = menu.findItem(R.id.acr)
        isong.title = "Lyrics"
        isong.icon = getDrawable(R.drawable.lyrics)

        songname.text = ""

    }

    fun change_view() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val menu = navigationView.menu
        val isong = menu.findItem(R.id.acr)

        if (isong.title == "Lyrics") {
            //change to second view
            firstview()
        } else {
            //change to first view
            secondview()
        }


    }

    fun rate_on_play() {
        val uri = Uri.parse("market://details?id=com.absan.verse")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.

        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.absan.verse")
                )
            )
        }
    }

    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.otherapps -> opengithub()
            R.id.acr -> change_view()
            R.id.open_spotify -> openspotify()
            R.id.helper -> versehelp()
            R.id.adblockmenu -> openadblock()
            R.id.rateapp -> rate_on_play()
            R.id.aboutapp -> aboutapp()
            R.id.feedback -> sendfeedback()
            R.id.mode -> changetheme()
            R.id.newfeatures -> whatsnewfrag()

            R.id.myname -> {
                Toast.makeText(this, "   [Appropriate Easter egg goes here]   ", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return true
    }

    //Members of IACRCloudlistener

    fun start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show()
            return
        }

        if (!mProcessing) {
            mProcessing = true
            mResult?.text = ""
            songname.text = ""
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false
                acrlyrics.text = "start error!"
            }
            startTime = System.currentTimeMillis()
        }
    }

    fun cancel() {
        if (mProcessing && this.mClient != null) {
            this.mClient.cancel()
        }

        this.reset()
    }

    fun reset() {
        acrlyrics.text = ""
        songname.text = ""
        mProcessing = false
    }

    fun handleResult(acrResult: String?): String {
        var res = ""

        try {
            val json = JSONObject(acrResult)
            val status: JSONObject = json.getJSONObject("status")
            val code = status.getInt("code")
            if (code == 0) {
                val metadata: JSONObject = json.getJSONObject("metadata")
                if (metadata.has("music")) {
                    val musics = metadata.getJSONArray("music")
                    val tt = musics[0] as JSONObject
                    val title = tt.getString("title")
                    val artistt = tt.getJSONArray("artists")
                    val art = artistt[0] as JSONObject
                    val artist = art.getString("name")
                    try {
                        songid = tt.getJSONObject("external_metadata").getJSONObject("spotify")
                            .getJSONObject("track").getString("id")
                    }catch (e:java.lang.Exception){songid = ""}

                    res = "$title - $artist - $songid"
                }
            } else {
                // TODO: Handle error
                res = acrResult.toString()
            }
        } catch (e: JSONException) {
            res = "Couldn't detect song :("
            Log.e("ACR", "JSONException", e)
        }
        return res
    }

    fun showctoast(det: String, art: String) {
        var li: LayoutInflater = layoutInflater
        var layout = li.inflate(R.layout.customtoast, findViewById(R.id.relativelay))
        var dettext = layout.findViewById<TextView>(R.id.details)
        var artname = layout.findViewById<TextView>(R.id.artist)
        dettext.text = det
        artname.text = art


        var t = Toast(this )
        t.duration = Toast.LENGTH_SHORT
        t.setGravity(Gravity.BOTTOM, 0, 30)
        t.view = layout
        //t.show()

        val toastDurationInMilliSeconds = 15000


        val toastCountDown: CountDownTimer
        toastCountDown =
            object : CountDownTimer(toastDurationInMilliSeconds.toLong(), 1000 /*Tick duration*/) {

                override fun onTick(millisUntilFinished: Long) {
                    t.show()
                }

                override fun onFinish() {
                    t.cancel()  
                }


            }

        t.show()
        toastCountDown.start()

    }
        
    fun playonspotify(sid: String) {
        val base_link = "spotify:track:" + sid.replace(" ", "")
        //println(base_link)
        val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(base_link))
        startActivity(launcher)
    }

    override fun onResult(results: ACRCloudResult?) {
        this.reset()

        val result = results?.getResult()
        startid.visibility = View.GONE
        startid2.visibility = View.VISIBLE
        pulsator.stop()
        acrcloud.bringToFront()
        songll.bringToFront()
        //println(result)


        var song_name = ""
        var artist_name = ""
        var code = 0

        try {
            song_name = handleResult(result).split("-")[0]
            artist_name = handleResult(result).split("-")[1]
            songid = handleResult(result).split("-")[2]
        } catch (e: java.lang.Exception) {
            if (this.hasWindowFocus() == false) {
                showctoast("Unable to find song", artist_name)
            }
        }
        if (song_name != "{\"status\":{\"msg\":\"No result\",\"code\":1001,\"version\":\"1.0\"}}") {
            songname.text = song_name
            if (songid!= " "){
                spot.visibility = View.VISIBLE
                spot.setOnClickListener { playonspotify(songid) }
            }
            else{
                spot.visibility = View.GONE

            }
           

        } else {
            songname.text = "Unable to find song"
            spot.visibility = View.GONE
        }

        if (this.hasWindowFocus() == false) {
            showctoast(song_name, artist_name)           
        }
        try {
            if (songname.text != "Unable to find song") {
                try_google_for_acr(song_name, artist_name)
            }
        } catch (e: java.lang.Exception) {
            acrlyrics.setText("")
            acrlyrics.setText("Please try different song. /nUnable to request lyrics for the given song")
        }


        startTime = System.currentTimeMillis()
    }

    override fun onVolumeChanged(curVolume: Double) {
        val time = (System.currentTimeMillis() - startTime) / 1000
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO
    )

    fun verifyPermissions() {
        for (i in PERMISSIONS.indices) {
            val permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS[i])
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, PERMISSIONS,
                    REQUEST_EXTERNAL_STORAGE
                )
                break
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("MainActivity", "release")
        if (this.mClient != null) {
            this.mClient.release()
            this.initState = false
        }

    }

}
