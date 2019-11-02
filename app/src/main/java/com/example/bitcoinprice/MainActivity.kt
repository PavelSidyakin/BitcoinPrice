package com.example.bitcoinprice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcoinprice.di.screen.BitcoinPriceScreenComponent
import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractor
import com.example.bitcoinprice.utils.logs.log
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bitcoinPriceInteractor: BitcoinPriceInteractor

    private var bitcoinPriceScreenComponent: BitcoinPriceScreenComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bitcoinPriceScreenComponent = TheApplication.getAppComponent().getBitcoinPriceScreenComponent();
        bitcoinPriceScreenComponent?.inject(this)

        setContentView(R.layout.activity_fullscreen)

    }

    override fun onResume() {
        super.onResume()
        bitcoinPriceInteractor.requestBitcoinMarketPrices(7)
            .doOnSubscribe { log { i("Test", "MainActivity.onCreate(): Subscribe. ") } }
            .doOnSuccess { log { i("Test", "MainActivity.onCreate(): Success. Result: $it") } }
            .doOnError { log { w("Test", "MainActivity.onCreate(): Error", it) } }
            .subscribe()

    }

    override fun onPause() {
        super.onPause()
        log { w("Test", "Destroy()") }

    }


}
