package com.absan.verse.Helpers.Classes

import android.app.Activity
import android.os.Handler
import android.os.Looper

class Run {
    companion object {
        val handler: Handler = Handler(Looper.getMainLooper())
        fun after(delay: Long, activity: Activity, process: () -> Unit) {

            handler.postDelayed({
                activity.runOnUiThread {
                    process()
                }
            }, delay)
        }
    }
}