package com.example.bitcoinprice.di.bitcoin_price

import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProviderImpl
import com.example.bitcoinprice.di.PerFeature
import dagger.Binds
import dagger.Module

@Module
abstract class BitcoinPriceModule {

    @PerFeature
    @Binds
    abstract fun provideBlockChainDataProvider(blockChainDataProvider: BlockChainDataProviderImpl): BlockChainDataProvider

}