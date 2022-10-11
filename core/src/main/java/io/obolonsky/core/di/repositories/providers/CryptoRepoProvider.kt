package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.CryptoRepo

interface CryptoRepoProvider {

    val cryptoRepo: CryptoRepo
}