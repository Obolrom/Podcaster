package io.obolonsky.podcaster.di.modules

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import io.obolonsky.podcaster.BuildConfig
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.shazam_feature.ShazamApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    @Provides
    fun provideShazamApi(
        @Shazam retrofit: Retrofit
    ): ShazamApi {
        return retrofit.create(ShazamApi::class.java)
    }

    @ApplicationScope
    @Provides
    fun provideBookApi(
        retrofit: Retrofit
    ): BookApi {
        return retrofit.create(BookApi::class.java)
    }
}

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Shazam