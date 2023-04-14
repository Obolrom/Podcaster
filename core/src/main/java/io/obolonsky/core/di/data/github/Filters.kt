package io.obolonsky.core.di.data.github

import androidx.annotation.StringRes
import io.obolonsky.core.R

enum class SortFilter(@StringRes val stringId: Int) {
    LAST_UPDATED(R.string.filter_sort_last_upd),
    NAME(R.string.filter_sort_name),
    STARS(R.string.filter_sort_stars),
}