package com.example.bitcoinprice.domain.bitcoin_price

import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import io.reactivex.Single

interface BitcoinPriceInteractor {

    fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesResult>

}