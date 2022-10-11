package io.obolonsky.crypto.di

import io.obolonsky.core.di.repositories.CryptoRepo
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class ScopedCryptoRepo @Inject constructor(
    private val cryptoRepo: CryptoRepo
) : CryptoRepo by cryptoRepo