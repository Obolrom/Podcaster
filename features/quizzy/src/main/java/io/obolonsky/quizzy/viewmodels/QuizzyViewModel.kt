package io.obolonsky.quizzy.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.NO_ID
import io.obolonsky.quizzy.data.*
import io.obolonsky.quizzy.redux.QuizScreenSideEffect
import io.obolonsky.quizzy.redux.QuizScreenState
import io.obolonsky.quizzy.redux.UiElementTypes.*
import io.obolonsky.quizzy.ui.components.*
import io.obolonsky.quizzy.usecases.GetLocalizationsUseCase
import io.obolonsky.quizzy.usecases.GetTemplateUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.util.*

@Suppress("unused_parameter")
class QuizzyViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getLocalizationsUseCase: GetLocalizationsUseCase,
    private val getTemplateUseCase: GetTemplateUseCase,
) : ViewModel(), ContainerHost<QuizScreenState, QuizScreenSideEffect> {

    override val container: Container<QuizScreenState, QuizScreenSideEffect> = container(
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
            } else if (component is RadioGroupUiElement
                && action is SelectRadioButtonAction
                && changedId == component.id) {
                component.copy(selectedId = action.selectedButtonId)
            } else if (component is InputUiElement
                && action is InputChangedAction
                && changedId == component.id) {
                component.copy(value = action.newValue)
            } else if (component is RowUiElement
                && component.subcomponents.find { it.id == changedId } is InputUiElement
                && action is InputChangedAction) {
                val reducedSubcomponents = component.subcomponents
                    .map { uiElement ->
                        if (uiElement.id == changedId && uiElement is InputUiElement)
                            uiElement.copy(value = action.newValue)
                        else
                            uiElement
                    }
                component.copy(subcomponents = reducedSubcomponents)
            } else {
                component
            }
        })

        reduce { reduced }
    }

    fun submit() = intent {
        val checkBoxData = state.uiElements
            ?.filterIsInstance<CheckBoxUiElement>()
            ?.fold(mutableMapOf<String, Boolean>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.isChecked) }
            }
            .orEmpty()

        val inputData = state.uiElements
            ?.filterIsInstance<InputUiElement>()
            ?.filter { it.value.isNotBlank() }
            ?.fold(mutableMapOf<String, String>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.value) }
            }
            .orEmpty()

        // TODO: add collecting for other types
        val inputSubfieldsData = state.uiElements
            ?.asSequence()
            ?.filterIsInstance<RowUiElement>()
            ?.map { it.subcomponents }
            ?.flatten()
            ?.filterIsInstance<InputUiElement>()
            ?.filter { it.value.isNotBlank() }
            ?.fold(mutableMapOf<String, String>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.value) }
            }
            .orEmpty()

        val radioData = state.uiElements
            ?.filterIsInstance<RadioGroupUiElement>()
            ?.filter { it.selectedId != NO_ID }
            ?.fold(mutableMapOf<String, String>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.selectedId) }
            }
            .orEmpty()

        val quizOutput = QuizOutput(
            id = UUID.randomUUID(),
            inputs = inputData + inputSubfieldsData,
            checkBoxes = checkBoxData,
            radioGroups = radioData,
        )

        Timber.d("quizOutput $quizOutput")

        val keys = quizOutput.inputs.keys +
                quizOutput.checkBoxes.keys +
                quizOutput.radioGroups.keys
        val requiredFields = (state.uiElements
            ?.filterIsInstance<Requireable>()
            ?.filter { it.required == true }
            .orEmpty() +
                state.uiElements
            ?.asSequence()
            ?.filterIsInstance<RowUiElement>()
            ?.map { it.subcomponents }
            ?.flatten()
            ?.filterIsInstance<Requireable>()
            ?.toList()
            ?.filter { it.required == true }
            .orEmpty())
            .map { it.id }

        if (!keys.containsAll(requiredFields)) {
            postSideEffect(QuizScreenSideEffect.NotAllRequiredFieldsAreFilled)
        }
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
                                        weight = field.weight,
                                    )
                                }
                                CHECKBOX -> {
                                    CheckBoxUiElement(
                                        id = field.id,
                                        type = field.type,
                                        label = localizations[field.labelKey] ?: "",
                                        isChecked = false,
                                        weight = field.weight,
                                        required = field.required,
                                    )
                                }
                                INPUT -> {
                                    InputUiElement(
                                        id = field.id,
                                        type = field.type,
                                        label = localizations[field.labelKey] ?: "",
                                        value = "",
                                        weight = field.weight,
                                        required = field.required,
                                    )
                                }
                                ROW -> {
                                    RowUiElement(
                                        id = field.id,
                                        type = field.type,
                                        label = localizations[field.labelKey] ?: "",
                                        subcomponents = field.subfields.map { subfield ->
                                            when (subfield.type) {
                                                TEXT_LABEL -> {
                                                    TextLabelUiElement(
                                                        type = subfield.type,
                                                        id = subfield.type.name,
                                                        label = localizations[subfield.labelKey] ?: "",
                                                        weight = subfield.weight,
                                                    )
                                                }
                                                CHECKBOX -> {
                                                    CheckBoxUiElement(
                                                        id = subfield.id,
                                                        type = subfield.type,
                                                        label = localizations[subfield.labelKey] ?: "",
                                                        isChecked = false,
                                                        weight = subfield.weight,
                                                        required = subfield.required,
                                                    )
                                                }
                                                INPUT -> {
                                                    InputUiElement(
                                                        id = subfield.id,
                                                        type = subfield.type,
                                                        label = localizations[subfield.labelKey] ?: "",
                                                        value = "",
                                                        weight = subfield.weight,
                                                        required = subfield.required,
                                                    )
                                                }
                                                ROW -> error("Not supported")
                                                RADIO -> error("Not supported")
                                            }
                                        }
                                    )
                                }
                                RADIO -> {
                                    RadioGroupUiElement(
                                        id = field.id,
                                        type = field.type,
                                        label = localizations[field.labelKey] ?: "",
                                        weight = field.weight,
                                        values = field.values?.map { radioValue ->
                                            RadioGroupUiElement.RadioButtonUiElement(
                                                id = radioValue.id,
                                                label = localizations[radioValue.labelKey] ?: "",
                                            )
                                        }.orEmpty(),
                                        selectedId = NO_ID,
                                        required = field.required,
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