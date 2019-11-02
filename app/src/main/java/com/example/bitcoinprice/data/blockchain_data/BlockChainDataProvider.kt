package com.example.bitcoinprice.data.blockchain_data

import com.example.bitcoinprice.data.blockchain_data.model.json.Result
import com.example.bitcoinprice.data.blockchain_data.model.Time
import io.reactivex.Single

interface BlockChainDataProvider {

    fun requestBitcoinMarketPrices(timeSpan: Time, rollingAverage: Time? = null): Single<Result>

}