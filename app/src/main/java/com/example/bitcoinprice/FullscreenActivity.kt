package com.example.bitcoinprice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.blockchain_data.model.BlockChainTime
import com.example.bitcoinprice.data.blockchain_data.model.BlockChainTimeUnit
import com.example.bitcoinprice.di.screen.BitcoinPriceScreenComponent
import com.example.bitcoinprice.utils.logs.log
import javax.inject.Inject

class FullscreenActivity : AppCompatActivity() {

    @Inject
    lateinit var blockChainDataProvider: BlockChainDataProvider

    private var bitcoinPriceScreenComponent: BitcoinPriceScreenComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        bitcoinPriceScreenComponent = TheApplication.getAppComponent().getBitcoinPriceScreenComponent();
        bitcoinPriceScreenComponent?.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        blockChainDataProvider.requestMarketPrices(BlockChainTime(24, BlockChainTimeUnit.HOUR),
            BlockChainTime(10, BlockChainTimeUnit.MINUTE))
                .doOnSubscribe { log { i("Test", "FullscreenActivity.onCreate(): Subscribe. savedInstanceState = [${savedInstanceState}]") } }
                .doOnSuccess { log { i("Test", "FullscreenActivity.onCreate(): Success. Result: $it") } }
                .doOnError { log { w("Test", "FullscreenActivity.onCreate(): Error", it) } }
                .subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        bitcoinPriceScreenComponent = null
    }

}
