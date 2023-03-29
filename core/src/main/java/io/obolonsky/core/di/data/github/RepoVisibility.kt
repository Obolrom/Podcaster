package io.obolonsky.core.di.data.github

import androidx.annotation.StringRes
import io.obolonsky.core.R as CoreR

enum class RepoVisibility(@StringRes val resId: Int) {
    PUBLIC(CoreR.string.visibility_public),
    PRIVATE(CoreR.string.visibility_private),
    INTERNAL(CoreR.string.visibility_internal),
}