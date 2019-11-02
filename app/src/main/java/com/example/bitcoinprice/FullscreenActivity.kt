package com.example.bitcoinprice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.blockchain_data.model.Time
import com.example.bitcoinprice.data.blockchain_data.model.TimeUnit
import com.example.bitcoinprice.di.screen.BitcoinPriceScreenComponent
import com.example.bitcoinprice.domain.data.BitcoinPriceRepository
import com.example.bitcoinprice.utils.logs.log
import javax.inject.Inject

class FullscreenActivity : AppCompatActivity() {

    @Inject
    lateinit var bitcoinPriceRepository: BitcoinPriceRepository

    private var bitcoinPriceScreenComponent: BitcoinPriceScreenComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

    }

    override fun onResume() {

        bitcoinPriceScreenComponent = TheApplication.getAppComponent().getBitcoinPriceScreenComponent();
        bitcoinPriceScreenComponent?.inject(this)

        super.onResume()
        bitcoinPriceRepository.requestBitcoinMarketPrices(7)
            .doOnSubscribe { log { i("Test", "FullscreenActivity.onCreate(): Subscribe. ") } }
            .doOnSuccess { log { i("Test", "FullscreenActivity.onCreate(): Success. Result: $it") } }
            .doOnError { log { w("Test", "FullscreenActivity.onCreate(): Error", it) } }
            .subscribe()

    }

    override fun onPause() {
        super.onPause()
        log { w("Test", "Destroy()") }
        bitcoinPriceScreenComponent = null

    }


}
