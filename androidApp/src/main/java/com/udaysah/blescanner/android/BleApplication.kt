package com.udaysah.blescanner.android

import android.app.Application
import com.udaysah.blescanner.android.di.bleModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BleApplication)
            modules(bleModule)
        }
    }
}
