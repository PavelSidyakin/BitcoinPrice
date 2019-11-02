package com.example.bitcoinprice.data.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

data class BlockChainRequestMarketPricesDataPoint (

    val x: Long, // Unix timestamp
    val y: Double // Price in USD

) : GsonSerializable