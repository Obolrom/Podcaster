package io.obolonsky.network.api

import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.banks.MonoAccountInfoResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface MonoBankApi {

    @Headers("X-Token: ${BuildConfig.MONO_BANK_TOKEN}")
    @GET("client-info")
    fun getAccountInfo(): Single<MonoAccountInfoResponse>
}