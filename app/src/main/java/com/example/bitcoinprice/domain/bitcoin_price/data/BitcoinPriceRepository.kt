package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.domain.TimePeriod
import io.reactivex.Single

interface BitcoinPriceRepository {

    fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesRequestResult>

}