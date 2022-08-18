package io.obolonsky.shazam_feature.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.shazam_feature.BuildConfig
import io.obolonsky.shazam_feature.PlainShazamApi
import io.obolonsky.shazam_feature.SongRecognitionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Qualifier

@Module
class ShazamModule {

    @FeatureScope
    @ShazamModuleFeature
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @FeatureScope
    @ShazamModuleFeature
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        }
    }

    @FeatureScope
    @ShazamModuleFeature
    @Provides
    fun provideOkHttpClient(
        @ShazamModuleFeature loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @FeatureScope
    @ShazamModuleFeature
    @Provides
    fun provideRetrofit(
        @ShazamModuleFeature client: OkHttpClient,
        @ShazamModuleFeature converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://diploma123-001-site1.ctempurl.com/api/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @FeatureScope
    @ShazamModulePlainFeature
    @Provides
    fun providePlainRetrofit(
        @ShazamModuleFeature client: OkHttpClient,
        @ShazamModuleFeature converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://localhost")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @FeatureScope
    @ShazamModuleFeature
    @Provides
    fun provideSongRecognitionApi(
        @ShazamModuleFeature retrofit: Retrofit,
    ): SongRecognitionApi = retrofit.create()

    @FeatureScope
    @ShazamModulePlainFeature
    @Provides
    fun providePlainApi(
        @ShazamModulePlainFeature retrofit: Retrofit,
    ): PlainShazamApi = retrofit.create()
}

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ShazamModuleFeature

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ShazamModulePlainFeature