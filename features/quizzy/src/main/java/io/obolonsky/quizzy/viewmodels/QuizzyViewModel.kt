package io.obolonsky.quizzy.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.quizzy.redux.QuizScreenState
import io.obolonsky.quizzy.redux.UiElementTypes.*
import io.obolonsky.quizzy.ui.components.CheckBoxUiElement
import io.obolonsky.quizzy.ui.components.TextLabelUiElement
import io.obolonsky.quizzy.ui.components.ToggleCheckBoxAction
import io.obolonsky.quizzy.ui.components.UiAction
import io.obolonsky.quizzy.usecases.GetLocalizationsUseCase
import io.obolonsky.quizzy.usecases.GetTemplateUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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
    private val getTemplateUseCase: GetTemplateUseCase,
) : ViewModel(), ContainerHost<QuizScreenState, Unit> {

    override val container: Container<QuizScreenState, Unit> = container(
        initialState = QuizScreenState(
            title = null,
        )
    )

    init {
        loadTemplate()
    }

    fun onAction(action: UiAction) = intent {
        val changedId = action.id
        val reduced = state.copy(uiElements = state.uiElements?.map { component ->
            if (component is CheckBoxUiElement
                && action is ToggleCheckBoxAction
                && changedId == component.id) {
                component.copy(isChecked = action.isChecked)
            }
            else component
        })

        reduce { reduced }
    }

    private fun loadTemplate() = intent {
        getTemplateUseCase.get("templates/first_quiz.json")
            .combine(getLocalizationsUseCase.get()) { template, localizations ->
                template to localizations
            }
            .onEach { (template, localizations) ->
                reduce {
                    state.copy(
                        template = template.copy(
                            fields = template.fields
                                .map { it.copy(labelKey = localizations[it.labelKey] ?: "") }
                        ),
                        uiElements = template.fields.map { field ->
                            when (field.type) {
                                TEXT_LABEL -> {
                                    TextLabelUiElement(
                                        type = field.type,
                                        id = field.type.name,
                                        label = localizations[field.labelKey] ?: "",
                                    )
                                }
                                CHECKBOX -> {
                                    CheckBoxUiElement(
                                        id = field.id,
                                        type = field.type,
                                        label = localizations[field.labelKey] ?: "",
                                        isChecked = false,
                                    )
                                }
                            }
                        }
                    )
                }
            }
            .collect()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): QuizzyViewModel
    }
}