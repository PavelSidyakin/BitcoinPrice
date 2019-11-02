package com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

data class DataPoint (

    val x: Long, // Unix timestamp
    val y: Double // Price in USD

) : GsonSerializable