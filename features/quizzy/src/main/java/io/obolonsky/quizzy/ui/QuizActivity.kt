package io.obolonsky.quizzy.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.quizzy.data.*
import io.obolonsky.quizzy.redux.QuizScreenSideEffect
import io.obolonsky.quizzy.redux.QuizScreenState
import io.obolonsky.quizzy.ui.components.*
import io.obolonsky.quizzy.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import io.obolonsky.core.R as CoreR

/*
 * reference - https://habr.com/ru/companies/alfa/articles/668754/
 */

class QuizActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val quizzyViewModel by lazyViewModel {
        componentViewModel.component
            .getQuizzyViewModelFactory()
            .create(it)
    }

    private val toaster by toaster()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun onSideEffect(effect: QuizScreenSideEffect) = when (effect) {
        is QuizScreenSideEffect.NotAllRequiredFieldsAreFilled -> {
            toaster.showToast(this, "Fill all fields")
        }
    }
}

@Composable
fun QuizScreen(
    state: QuizScreenState,
    onAction: (UiAction) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) = Surface(modifier = modifier) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        state.uiElements?.forEach { field ->
            when (field) {
                is TextLabelUiElement -> {
                    TextComponent(
                        modifier = Modifier
                            .padding(PaddingValues(
                                start = (field.paddings?.start ?: 0).dp,
                                end = (field.paddings?.end ?: 0).dp,
                                top = (field.paddings?.top ?: 0).dp,
                                bottom = (field.paddings?.bottom ?: 0).dp,
                            )),
                        uiElement = field,
                    )
                }
                is CheckBoxUiElement -> {
                    CheckBoxComponent(
                        modifier = Modifier
                            .padding(PaddingValues(
                                start = (field.paddings?.start ?: 0).dp,
                                end = (field.paddings?.end ?: 0).dp,
                                top = (field.paddings?.top ?: 0).dp,
                                bottom = (field.paddings?.bottom ?: 0).dp,
                            )),
                        uiElement = field,
                        onAction = onAction,
                    )
                }
                is InputUiElement -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InputComponent(
                            modifier = Modifier
                                .then(
                                    if (field.weight != null) Modifier.weight(field.weight!!)
                                    else Modifier
                                )
                                .padding(
                                    PaddingValues(
                                        start = (field.paddings?.start ?: 0).dp,
                                        end = (field.paddings?.end ?: 0).dp,
                                        top = (field.paddings?.top ?: 0).dp,
                                        bottom = (field.paddings?.bottom ?: 0).dp,
                                    )
                                ),
                            uiElement = field,
                            onAction = onAction,
                        )
                    }
                }
                is RowUiElement -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = (field.paddings?.start ?: 0).dp,
                                    end = (field.paddings?.end ?: 0).dp,
                                    top = (field.paddings?.top ?: 0).dp,
                                    bottom = (field.paddings?.bottom ?: 0).dp,
                                )
                            ),
                    ) {
                        field.subcomponents.forEach { subcomponent ->
                            when (subcomponent) {
                                is TextLabelUiElement -> {
                                    TextComponent(
                                        modifier = Modifier
                                            .then(
                                                if (subcomponent.weight != null) Modifier.weight(
                                                    subcomponent.weight!!
                                                )
                                                else Modifier
                                            )
                                            .padding(
                                                PaddingValues(
                                                    start = (subcomponent.paddings?.start ?: 0).dp,
                                                    end = (subcomponent.paddings?.end ?: 0).dp,
                                                    top = (subcomponent.paddings?.top ?: 0).dp,
                                                    bottom = (subcomponent.paddings?.bottom
                                                        ?: 0).dp,
                                                )
                                            ),
                                        uiElement = subcomponent,
                                    )
                                }
                                is CheckBoxUiElement -> {
                                    CheckBoxComponent(
                                        modifier = Modifier
                                            .then(
                                                if (subcomponent.weight != null) Modifier.weight(
                                                    subcomponent.weight!!
                                                )
                                                else Modifier
                                            )
                                            .padding(
                                                PaddingValues(
                                                    start = (subcomponent.paddings?.start ?: 0).dp,
                                                    end = (subcomponent.paddings?.end ?: 0).dp,
                                                    top = (subcomponent.paddings?.top ?: 0).dp,
                                                    bottom = (subcomponent.paddings?.bottom
                                                        ?: 0).dp,
                                                )
                                            ),
                                        uiElement = subcomponent,
                                        onAction = onAction,
                                    )
                                }
                                is InputUiElement -> {
                                    InputComponent(
                                        modifier = Modifier
                                            .then(
                                                if (subcomponent.weight != null) Modifier.weight(
                                                    subcomponent.weight!!
                                                )
                                                else Modifier
                                            )
                                            .padding(
                                                PaddingValues(
                                                    start = (subcomponent.paddings?.start ?: 0).dp,
                                                    end = (subcomponent.paddings?.end ?: 0).dp,
                                                    top = (subcomponent.paddings?.top ?: 0).dp,
                                                    bottom = (subcomponent.paddings?.bottom
                                                        ?: 0).dp,
                                                )
                                            ),
                                        uiElement = subcomponent,
                                        onAction = onAction,
                                    )
                                }
                                is RowUiElement -> error("Not supported")
                                is RadioGroupUiElement -> {
                                    RadioGroupComponent(
                                        modifier = Modifier
                                            .then(
                                                if (subcomponent.weight != null) Modifier.weight(
                                                    subcomponent.weight!!
                                                )
                                                else Modifier
                                            )
                                            .padding(
                                                PaddingValues(
                                                    start = (subcomponent.paddings?.start ?: 0).dp,
                                                    end = (subcomponent.paddings?.end ?: 0).dp,
                                                    top = (subcomponent.paddings?.top ?: 0).dp,
                                                    bottom = (subcomponent.paddings?.bottom
                                                        ?: 0).dp,
                                                )
                                            ),
                                        uiElement = subcomponent,
                                        onAction = onAction,
                                    )
                                }
                            }
                        }
                    }
                }
                is RadioGroupUiElement -> {
                    RadioGroupComponent(
                        modifier = Modifier
                            .padding(PaddingValues(
                                start = (field.paddings?.start ?: 0).dp,
                                end = (field.paddings?.end ?: 0).dp,
                                top = (field.paddings?.top ?: 0).dp,
                                bottom = (field.paddings?.bottom ?: 0).dp,
                            )),
                        uiElement = field,
                        onAction = onAction,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = onSubmit,
            ) {
                Text(
                    text = stringResource(CoreR.string.quiz_submit),
                )
            }
        }
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
        ),
        onAction = { },
        onSubmit = { },
    )
}