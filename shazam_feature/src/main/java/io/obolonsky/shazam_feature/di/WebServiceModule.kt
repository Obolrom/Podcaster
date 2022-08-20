package io.obolonsky.shazam_feature.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.shazam_feature.BuildConfig
import io.obolonsky.shazam_feature.data.PlainShazamApi
import io.obolonsky.shazam_feature.data.SongRecognitionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
internal class WebServiceModule {

    @FeatureScope
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @FeatureScope
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        }
    }

    @FeatureScope
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @FeatureScope
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

    @FeatureScope
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

    @FeatureScope
    @Provides
    fun provideSongRecognitionApi(
        retrofit: Retrofit,
    ): SongRecognitionApi = retrofit.create()

    @FeatureScope
    @Provides
    fun providePlainApi(
        @ShazamPlain retrofit: Retrofit,
    ): PlainShazamApi = retrofit.create()
}