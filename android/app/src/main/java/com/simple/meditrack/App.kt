package com.simple.meditrack

import android.app.Application
import android.content.Context
import com.simple.meditrack.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    companion object {
        lateinit var shared: App
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        startKoin {

            androidContext(this@App)

            androidLogger(Level.NONE)

            modules(
//                appModule,
//
//                apiModule,
//
//                daoModule,
//
//                cacheModule,
//
//                memoryModule,
//
//                coreCacheModule,
//
//                realtimeModule,
//                appModule,
//                usecaseModule,
                viewModelModule,
//                repositoryModule
            )
        }

    }

    override fun onCreate() {
        shared = this

        super.onCreate()
    }
}