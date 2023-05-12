package io.obolonsky.quizzy.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.quizzy.redux.QuizScreenState
import io.obolonsky.quizzy.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState

class QuizActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val quizzyViewModel by lazyViewModel {
        componentViewModel.component
            .getQuizzyViewModelFactory()
            .create(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizzyViewModel.test()

        setContent {
            val state = quizzyViewModel.collectAsState()

            QuizScreen(
                state = state.value,
            )
        }
    }
}

@Composable
fun QuizScreen(
    state: QuizScreenState,
    modifier: Modifier = Modifier,
) = Surface(modifier = modifier) {
    if (state.title != null) {
        Text(text = state.title)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun QuizScreenPreview() {
    QuizScreen(
        state = QuizScreenState(
            title = "Quizzy",
        )
    )
}