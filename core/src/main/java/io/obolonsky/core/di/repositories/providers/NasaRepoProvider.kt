package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.NasaRepo

interface NasaRepoProvider {

    val nasaRepo: NasaRepo
}