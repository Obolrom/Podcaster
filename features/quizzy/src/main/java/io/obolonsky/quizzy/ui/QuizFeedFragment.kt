package io.obolonsky.quizzy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.quizzy.R
import io.obolonsky.quizzy.data.QuizOutput
import io.obolonsky.quizzy.redux.QuizFeedSideEffects
import io.obolonsky.quizzy.redux.QuizFeedState
import io.obolonsky.quizzy.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.*

class QuizFeedFragment : Fragment() {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val quizFeedViewModel by lazyViewModel {
        componentViewModel.component
            .getQuizFeedViewModel()
            .create(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                val state = quizFeedViewModel.collectAsState()
                quizFeedViewModel.collectSideEffect(sideEffect = ::onSideEffect)

                QuizFeedScreen(
                    state = state.value,
                    onCardClick = quizFeedViewModel::navigateToQuizScreen,
                )
            }
        }
    }

    private fun onSideEffect(effect: QuizFeedSideEffects): Unit = when (effect) {
        is QuizFeedSideEffects.NavigateToQuizSideEffect -> {
            findNavController()
                .navigate(
                    resId = R.id.action_quizFeedFragment_to_quizFragment,
                    args = bundleOf(
                        "quizId" to effect.quizId.toString()
                    ),
                )
        }
    }
}

@Composable
fun QuizFeedScreen(
    state: QuizFeedState,
    onCardClick: (UUID) -> Unit,
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier.fillMaxSize(),
) {
    LazyColumn {
        items(state.feed) { quiz ->
            QuizCard(
                modifier = Modifier
                    .clickable { onCardClick(quiz.id) }
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                quiz = quiz,
            )
        }
    }
}

@Composable
fun QuizCard(
    quiz: QuizOutput,
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier,
) {
    Text(
        text = quiz.id.toString(),
        style = MaterialTheme.typography.h6,
    )
}