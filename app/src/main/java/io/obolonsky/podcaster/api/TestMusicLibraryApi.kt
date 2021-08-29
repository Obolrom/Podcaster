package io.obolonsky.podcaster.api

import io.obolonsky.podcaster.data.responses.MediaResponse
import retrofit2.Call
import retrofit2.http.GET

interface TestMusicLibraryApi {

    @GET("master/models.json")
    fun getMusic(): Call<MediaResponse>
}