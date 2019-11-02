package com.example.bitcoinprice.data.bitcoin_price.blockchain_data

import java.lang.RuntimeException

class BlockChainDataError(val code: Code): RuntimeException(code.name) {

    enum class Code {
        NETWORK_ERROR,
        GENERAL_ERROR,
    }
}