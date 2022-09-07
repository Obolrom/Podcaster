package io.obolonsky.network.di.modules

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.api.FeatureTogglesApi
import io.obolonsky.network.api.MarsPhotosApi
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.utils.FeatureToggles
import io.obolonsky.network.utils.MarsPhotos
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

    @FeatureToggles
    @Provides
    fun provideFeatureTogglesRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/Obolrom/PodcasterFeatureToggles/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @MarsPhotos
    @Provides
    fun provideMarsPhotosRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/mars-photos/api/v1/")
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
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

    @Provides
    fun provideFeatureTogglesApi(
        @FeatureToggles retrofit: Retrofit,
    ): FeatureTogglesApi = retrofit.create()

    @Provides
    fun provideMarsPhotosApi(
        @MarsPhotos retrofit: Retrofit,
    ): MarsPhotosApi = retrofit.create()

    @Provides
    fun provideApolloClient(
        okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.spacex.land/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}