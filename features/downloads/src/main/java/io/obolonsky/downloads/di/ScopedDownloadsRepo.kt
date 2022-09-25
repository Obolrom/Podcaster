package io.obolonsky.downloads.di

import io.obolonsky.core.di.repositories.DownloadsRepo
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class ScopedDownloadsRepo @Inject constructor(
    repo: DownloadsRepo
) : DownloadsRepo by repo