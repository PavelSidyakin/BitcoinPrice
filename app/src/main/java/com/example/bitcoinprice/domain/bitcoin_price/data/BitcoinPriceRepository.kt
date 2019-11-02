package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import io.reactivex.Single

interface BitcoinPriceRepository {

    fun requestBitcoinMarketPrices(periodBeforeTodayDays: Int): Single<BitcoinPricesRequestResult>

}