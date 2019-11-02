package com.example.bitcoinprice.data.blockchain_data

import com.example.bitcoinprice.data.blockchain_data.model.json.BlockChainRequestMarketPricesResult
import com.example.bitcoinprice.data.blockchain_data.model.BlockChainTime
import com.example.bitcoinprice.utils.logs.log
import com.example.bitcoinprice.utils.rx.SchedulersProvider
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject

class BlockChainDataProviderImpl
    @Inject
    constructor(private val schedulersProvider: SchedulersProvider): BlockChainDataProvider {

    private val retrofit: Retrofit by lazy { createRetrofit() }


    override fun requestMarketPrices(timeSpan: BlockChainTime, rollingAverage: BlockChainTime): Single<BlockChainRequestMarketPricesResult> {
        return Single.fromCallable { createRequestMarketPricesService() }
            .flatMap { service -> service.requestMarketPrices(timeSpan.asString(), rollingAverage.asString()) }
            .onErrorResumeNext { throwable -> handleError(throwable) }
            .doOnSubscribe { log { i(TAG, "BlockChainDataProviderImpl.requestMarketPrices(): Subscribe. timeSpan = [${timeSpan}], rollingAverage = [${rollingAverage}]") } }
            .doOnSuccess { log { i(TAG, "BlockChainDataProviderImpl.requestMarketPrices(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BlockChainDataProviderImpl.requestMarketPrices(): Error", it) } }
            .subscribeOn(schedulersProvider.io())

    }

    private fun handleError(throwable: Throwable): Single<BlockChainRequestMarketPricesResult> {
        return when(throwable) {
            is IOException -> Single.error(BlockChainDataError(BlockChainDataError.Code.NETWORK_ERROR))
            else -> Single.error(BlockChainDataError(BlockChainDataError.Code.GENERAL_ERROR))
        }
    }

    private fun createRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor { message ->
            log { i(TAG, message) }
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.blockchain.info/charts/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulersProvider.io()))
            .client(client)
            .build()
    }

    private fun createRequestMarketPricesService(): RequestMarketPricesService {
        return retrofit.create(RequestMarketPricesService::class.java)
    }

    private interface RequestMarketPricesService {
        @GET("market-price/")
        fun requestMarketPrices(@Query("timespan") timeSpan: String,
                                @Query("rollingAverage") rollingAverage: String)
                : Single<BlockChainRequestMarketPricesResult>
    }

    private fun BlockChainTime.asString(): String {
        return "${unitCount}${timeUnit.value}"
    }

    companion object {
        private const val TAG = "BlockChainDataProvider"
    }

}