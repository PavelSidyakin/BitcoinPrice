package com.example.bitcoinprice.domain.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import io.reactivex.Single

interface BitcoinPriceRepository {

    fun requestBitcoinMarketPrices(periodBeforeTodayDays: Int): Single<BitcoinPricesRequestResult>

}