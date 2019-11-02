package com.example.bitcoinprice

import android.app.Application
import com.example.bitcoinprice.di.app.AppComponent
import com.example.bitcoinprice.di.app.DaggerAppComponent

class TheApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()

        appComponent.inject(this)

    }

    companion object {
        private lateinit var appComponent: AppComponent

        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }
}