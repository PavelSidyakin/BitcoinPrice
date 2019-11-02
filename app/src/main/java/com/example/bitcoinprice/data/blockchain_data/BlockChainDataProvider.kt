package com.example.bitcoinprice.data.blockchain_data

import com.example.bitcoinprice.data.blockchain_data.model.json.BlockChainRequestMarketPricesResult
import com.example.bitcoinprice.data.blockchain_data.model.BlockChainTime
import io.reactivex.Single

interface BlockChainDataProvider {

    fun requestMarketPrices(timeSpan: BlockChainTime, rollingAverage: BlockChainTime): Single<BlockChainRequestMarketPricesResult>

}