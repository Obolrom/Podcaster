package io.obolonsky.quizzy.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.quizzy.redux.QuizFeedSideEffects
import io.obolonsky.quizzy.redux.QuizFeedState
import io.obolonsky.quizzy.repositories.QuizOutputRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import java.util.UUID

@Suppress("unused_parameter")
class QuizFeedViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val quizOutputRepository: QuizOutputRepository,
) : ViewModel(), ContainerHost<QuizFeedState, QuizFeedSideEffects> {

    override val container: Container<QuizFeedState, QuizFeedSideEffects> = container(
        initialState = QuizFeedState(
            feed = emptyList(),
        )
    )

    init {
        observeSavedQuizzes()
    }

    fun navigateToQuizScreen(quizId: UUID) = intent {
        postSideEffect(QuizFeedSideEffects.NavigateToQuizSideEffect(quizId))
    }

    private fun observeSavedQuizzes() = intent(registerIdling = false) {
        repeatOnSubscription {
            quizOutputRepository.getSavedQuizzes()
                .onEach { feed ->
                    reduce { state.copy(feed = feed) }
                }
                .collect()
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): QuizFeedViewModel
    }
}