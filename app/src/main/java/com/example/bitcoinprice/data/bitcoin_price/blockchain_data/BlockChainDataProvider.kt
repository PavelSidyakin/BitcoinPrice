package com.example.bitcoinprice.data.bitcoin_price.blockchain_data

import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json.Result
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.Time
import io.reactivex.Single

interface BlockChainDataProvider {

    /**
     * Requests bitcoin marker prices.
     *
     * @param timePeriod Requested period.
     * @param rollingAverage Rolling average period. If null no rolling average is applied.
     *
     * @return Single with request result.
     *
     * Subscribe: io
     * Error: [BlockChainDataError]
     *
     */
    fun requestBitcoinMarketPrices(timePeriod: Time, rollingAverage: Time? = null): Single<Result>

}