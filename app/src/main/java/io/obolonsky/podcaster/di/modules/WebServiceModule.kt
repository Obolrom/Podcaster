package io.obolonsky.podcaster.di.modules

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.podcaster.BuildConfig
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.shazam_feature.PlainShazamApi
import io.obolonsky.shazam_feature.ShazamApi
import io.obolonsky.shazam_feature.SongRecognitionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Qualifier

@Module
class WebServiceModule {

    @ApplicationScope
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        }
    }

    @ApplicationScope
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @ApplicationScope
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://diploma123-001-site1.ctempurl.com/api/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @ApplicationScope
    @Shazam
    @Provides
    fun provideShazamRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://shazam.p.rapidapi.com/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @ApplicationScope
    @ShazamCore
    @Provides
    fun provideShazamCoreRetrofit(
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

    @ApplicationScope
    @Plain
    @Provides
    fun providePlainRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://localhost")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideShazamApi(
        @Shazam retrofit: Retrofit
    ): ShazamApi = retrofit.create()

    @ApplicationScope
    @Provides
    fun provideShazamCoreApi(
        @ShazamCore retrofit: Retrofit,
    ): SongRecognitionApi = retrofit.create()

    @ApplicationScope
    @Provides
    fun providePlainApi(
        @Plain retrofit: Retrofit,
    ): PlainShazamApi = retrofit.create()

    @ApplicationScope
    @Provides
    fun provideBookApi(
        retrofit: Retrofit
    ): BookApi = retrofit.create()
}

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Shazam

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Plain

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ShazamCore