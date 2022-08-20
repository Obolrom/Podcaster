package io.obolonsky.shazam_feature.di

import dagger.Module

@Module(
    includes = [
        WebServiceModule::class,
    ]
)
internal class ShazamModule