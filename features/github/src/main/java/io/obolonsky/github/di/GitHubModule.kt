package io.obolonsky.github.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.github.AuthorizationFailedInterceptor
import io.obolonsky.github.GithubApi
import io.obolonsky.github.TokenStorage
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import timber.log.Timber

@Module
class GitHubModule {

    @Provides
    fun provideAuthorizationService(context: Context): AuthorizationService {
        return AuthorizationService(context)
    }

    @FeatureScope
    @Provides
    fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(
                HttpLoggingInterceptor {
                    Timber.tag("Network").d(it)
                }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addNetworkInterceptor(AuthorizationInterceptor())
            .addNetworkInterceptor(AuthorizationFailedInterceptor(AuthorizationService(context), TokenStorage))
            .build()
    }

    @FeatureScope
    @Provides
    fun provideGitHubApi(okHttpClient: OkHttpClient): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create()
    }
}

class AuthorizationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
            .addTokenHeader()
            .let { chain.proceed(it) }
    }

    private fun Request.addTokenHeader(): Request {
        val authHeaderName = "Authorization"
        return newBuilder()
            .apply {
                val token = TokenStorage.accessToken
                if (token != null) {
                    header(authHeaderName, token.withBearer())
                }
            }
            .build()
    }

    private fun String.withBearer() = "Bearer $this"
}