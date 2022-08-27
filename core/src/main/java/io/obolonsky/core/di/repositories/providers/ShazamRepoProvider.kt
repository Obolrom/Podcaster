package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.ShazamRepo

interface ShazamRepoProvider {

    val shazamRepo: ShazamRepo
}