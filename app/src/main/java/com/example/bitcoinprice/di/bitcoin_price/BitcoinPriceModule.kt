package com.example.bitcoinprice.di.bitcoin_price

import com.example.bitcoinprice.data.bitcoin_price.BitcoinPriceCacheRepositoryImpl
import com.example.bitcoinprice.data.bitcoin_price.BitcoinPriceRepositoryImpl
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.BlockChainDataProviderImpl
import com.example.bitcoinprice.di.PerFeature
import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractor
import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractorImpl
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceCacheRepository
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceRepository
import dagger.Binds
import dagger.Module

@Module
abstract class BitcoinPriceModule {

    @PerFeature
    @Binds
    abstract fun provideBlockChainDataProvider(blockChainDataProvider: BlockChainDataProviderImpl): BlockChainDataProvider

    @PerFeature
    @Binds
    abstract fun provideBitcoinPriceRepository(bitcoinPriceRepository: BitcoinPriceRepositoryImpl): BitcoinPriceRepository

    @PerFeature
    @Binds
    abstract fun provideBitcoinPriceCacheRepository(bitcoinPriceCacheRepository: BitcoinPriceCacheRepositoryImpl): BitcoinPriceCacheRepository

    @PerFeature
    @Binds
    abstract fun provideBitcoinPriceInteractor(bitcoinPricesInteractor: BitcoinPriceInteractorImpl): BitcoinPriceInteractor

}