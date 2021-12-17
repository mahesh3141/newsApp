package com.robosoft.news.applications

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

class NewsApp : Application(), Application.ActivityLifecycleCallbacks  {
    companion object {
        @SuppressLint("StaticFieldLeak") //Handled by making currentActivity to null
        lateinit var ctx: NewsApp
    }
    var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if (activity == currentActivity) {
            currentActivity = null
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}