package io.obolonsky.quizzy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.quizzy.redux.QuizScreenSideEffect
import io.obolonsky.quizzy.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.UUID

class QuizFragment : Fragment() {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val quizzyViewModel by lazyViewModel {
        componentViewModel.component
            .getQuizzyViewModelFactory()
            .create(it, quizId)
    }

    private val toaster by toaster()

    private val quizId by lazy(LazyThreadSafetyMode.NONE) {
        UUID.fromString(arguments!!.getString("quizId")!!)
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
                val state = quizzyViewModel.collectAsState()
                quizzyViewModel.collectSideEffect(sideEffect = ::onSideEffect)

                QuizScreen(
                    state = state.value,
                    onAction = quizzyViewModel::onAction,
                    onSubmit = quizzyViewModel::submit,
                )
            }
        }
    }

    private fun onSideEffect(effect: QuizScreenSideEffect) = when (effect) {
        is QuizScreenSideEffect.NotAllRequiredFieldsAreFilled -> {
            toaster.showToast(requireContext(), "Fill all fields")
        }
    }
}