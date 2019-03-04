package com.bulbstudios.jobapplicator

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Created by Terence Baker on 04/03/2019.
 */
class JobApplicatorApplication : Application() {

    companion object {

        var refWatcher: RefWatcher by Delegates.notNull()
            private set
    }

    override fun onCreate() {

        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
        refWatcher = LeakCanary.install(this)


        if (BuildConfig.DEBUG) {

            Timber.plant(Timber.DebugTree())
        }
    }
}