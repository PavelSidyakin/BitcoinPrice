package com.example.bitcoinprice.di.bitcoin_price

import com.example.bitcoinprice.data.BitcoinPriceCacheRepositoryImpl
import com.example.bitcoinprice.data.BitcoinPriceRepositoryImpl
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProviderImpl
import com.example.bitcoinprice.di.PerFeature
import com.example.bitcoinprice.domain.BitcoinPriceInteractor
import com.example.bitcoinprice.domain.BitcoinPriceInteractorImpl
import com.example.bitcoinprice.domain.data.BitcoinPriceCacheRepository
import com.example.bitcoinprice.domain.data.BitcoinPriceRepository
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