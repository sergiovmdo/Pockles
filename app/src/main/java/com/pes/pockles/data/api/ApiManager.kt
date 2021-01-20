package com.pes.pockles.data.api

import com.pes.pockles.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiManager constructor(
    private val tokenAuthenticator: TokenAuthenticator
) {

    companion object {
        const val PROD_URL = "https://us-central1-pockles.cloudfunctions.net/api/"
        const val DEV_URL = "http://10.0.2.2:5001/pockles/us-central1/api/"

        const val APP_CLIENT_HEADER_NAME = "AppClient"
        const val APP_CLIENT_VALUE = "PockleS"
    }

    /**
     * Creates an API service of type [service] and adds interceptors to
     * add necessary data to the headers
     */
    fun <T> createApi(service: Class<T>): T {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE

        val appClientInterceptor = Interceptor { chain: Interceptor.Chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader(APP_CLIENT_HEADER_NAME, APP_CLIENT_VALUE)
            requestBuilder.addHeader(
                TokenAuthenticator.AUTH_HEADER_NAME,
                tokenAuthenticator.wrapToken()
            )
            chain.proceed(requestBuilder.build())
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(appClientInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PROD_URL)
            .client(client)
            .build()

        return retrofit.create(service)
    }
}