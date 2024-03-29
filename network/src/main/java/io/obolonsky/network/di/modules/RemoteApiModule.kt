package io.obolonsky.network.di.modules

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.api.*
import io.obolonsky.network.apihelpers.github.TokenStorage
import io.obolonsky.network.interceptors.github.GithubAuthorizationFailedInterceptor
import io.obolonsky.network.interceptors.github.GithubAuthorizationInterceptor
import io.obolonsky.network.utils.*
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
class RemoteApiModule {

    @ApplicationScope
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

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

    @Reusable
    @GitHub
    @Provides
    fun provideGitHubOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        context: Context,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .addNetworkInterceptor(Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("X-Github-Next-Global-ID", "1")
                    .build()
                chain.proceed(request)
            })
            .addNetworkInterceptor(GithubAuthorizationInterceptor())
            .addNetworkInterceptor(GithubAuthorizationFailedInterceptor(AuthorizationService(context), TokenStorage))
            .build()
    }

    @Reusable
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://shazam-api6.p.rapidapi.com/shazam/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(converter)
            .build()
    }

    @Reusable
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

    @Reusable
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

    @Reusable
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

    @Reusable
    @Apod
    @Provides
    fun provideNasaApodRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    @Reusable
    @PrivateBank
    @Provides
    fun providePrivateBankRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.privatbank.ua/p24api/")
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    @Reusable
    @MonoBank
    @Provides
    fun provideMonoBankRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.monobank.ua/personal/")
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    @Reusable
    @CoinPaprika
    @Provides
    fun provideCoinPaprikaRetrofit(
        client: OkHttpClient,
        converter: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coinpaprika.com/v1/")
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(converter)
            .build()
    }

    @Reusable
    @Provides
    fun provideGithubApi(
        @GitHub githubOkHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(githubOkHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create()
    }

    @Reusable
    @Provides
    fun provideSongRecognitionApi(
        retrofit: Retrofit,
    ): SongRecognitionApi = retrofit.create()

    @Reusable
    @Provides
    fun providePlainApi(
        @ShazamPlain retrofit: Retrofit,
    ): PlainShazamApi = retrofit.create()

    @Reusable
    @Provides
    fun provideFeatureTogglesApi(
        @FeatureToggles retrofit: Retrofit,
    ): FeatureTogglesApi = retrofit.create()

    @Reusable
    @Provides
    fun provideMarsPhotosApi(
        @MarsPhotos retrofit: Retrofit,
    ): MarsPhotosApi = retrofit.create()

    @Reusable
    @Provides
    fun provideNasaApodApi(
        @Apod retrofit: Retrofit,
    ): NasaApodApi = retrofit.create()

    @Reusable
    @Provides
    fun providePrivateBankApi(
        @PrivateBank retrofit: Retrofit,
    ): PrivateBankApi = retrofit.create()

    @Reusable
    @Provides
    fun provideMonoBankApi(
        @MonoBank retrofit: Retrofit,
    ): MonoBankApi = retrofit.create()

    @Reusable
    @Provides
    fun provideCoinPaprikaApi(
        @CoinPaprika retrofit: Retrofit,
    ): CoinPaprikaApi = retrofit.create()

    @Reusable
    @Provides
    fun provideApolloClient(
        okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.spacex.land/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }

    @Reusable
    @GitHub
    @Provides
    fun provideGithubApolloClient(
        @GitHub okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}