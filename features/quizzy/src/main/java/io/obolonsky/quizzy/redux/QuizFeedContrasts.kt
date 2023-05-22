package io.obolonsky.quizzy.redux

import io.obolonsky.quizzy.data.QuizOutput
import java.util.UUID

data class QuizFeedState(
    val feed: List<QuizOutput>,
)

sealed class QuizFeedSideEffects {

    data class NavigateToQuizSideEffect(val quizId: UUID) : QuizFeedSideEffects()
}