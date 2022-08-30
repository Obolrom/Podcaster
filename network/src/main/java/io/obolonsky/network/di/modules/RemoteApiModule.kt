package io.obolonsky.network.di.modules

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.utils.ShazamPlain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
class RemoteApiModule {

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://song-recognition.p.rapidapi.com/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @ShazamPlain
    @Provides
    fun providePlainRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://localhost")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @Provides
    fun provideSongRecognitionApi(
        retrofit: Retrofit,
    ): SongRecognitionApi = retrofit.create()

    @Provides
    fun providePlainApi(
        @ShazamPlain retrofit: Retrofit,
    ): PlainShazamApi = retrofit.create()
}