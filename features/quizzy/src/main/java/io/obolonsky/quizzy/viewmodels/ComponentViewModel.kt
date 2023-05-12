package io.obolonsky.quizzy.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.quizzy.di.DaggerQuizzyComponent
import io.obolonsky.quizzy.di.QuizzyComponent

internal class ComponentViewModel(application: Application) : AndroidViewModel(application) {

    val component: QuizzyComponent by lazy {
        DaggerQuizzyComponent.factory()
            .create((application as App).getAppComponent())
    }
}