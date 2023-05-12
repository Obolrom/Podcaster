package io.obolonsky.quizzy.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.quizzy.redux.QuizScreenState
import io.obolonsky.quizzy.usecases.GetLocalizationsUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

@Suppress("unused_parameter")
class QuizzyViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getLocalizationsUseCase: GetLocalizationsUseCase,
) : ViewModel(), ContainerHost<QuizScreenState, Unit> {

    override val container: Container<QuizScreenState, Unit> = container(
        initialState = QuizScreenState(
            title = null,
        )
    )

    init {
        test()
    }

    fun test() = intent {
        getLocalizationsUseCase.get()
            .onEach { localizations ->
                reduce { state.copy(title = localizations["quiz_title"] ?: "") }
            }
            .collect()
    }


    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): QuizzyViewModel
    }
}