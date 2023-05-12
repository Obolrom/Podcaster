package io.obolonsky.quizzy.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.quizzy.viewmodels.QuizzyViewModel

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
    ]
)
interface QuizzyComponent {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
        ): QuizzyComponent
    }

    fun getQuizzyViewModelFactory(): QuizzyViewModel.Factory
}