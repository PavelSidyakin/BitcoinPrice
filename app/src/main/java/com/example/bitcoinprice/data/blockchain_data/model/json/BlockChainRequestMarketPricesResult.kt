package com.example.bitcoinprice.data.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

data class BlockChainRequestMarketPricesResult (

    val status: String,
    val name: String,
    val period: String,
    val description: String,
    val values: List<BlockChainRequestMarketPricesDataPoint>

) : GsonSerializable