package io.obolonsky.github.redux.userinfo

import android.content.Intent
import androidx.annotation.StringRes
import io.obolonsky.core.di.data.github.GithubDay
import io.obolonsky.core.di.data.github.GithubUserProfile

data class UserInfoState(
    val isLoading: Boolean,
    val user: GithubUserProfile?,
)

sealed class UserInfoSideEffects {

    data class ToastEvent(@StringRes val stringRes: Int) : UserInfoSideEffects()

    data class LogoutPageEvent(val intent: Intent) : UserInfoSideEffects()

    data class ChartDayEvent(val day: GithubDay) : UserInfoSideEffects()

    object LogoutPageCompletedEvent : UserInfoSideEffects()
}