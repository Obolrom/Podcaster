package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.BanksRepo

interface BanksRepoProvider {

    val banksRepo: BanksRepo
}