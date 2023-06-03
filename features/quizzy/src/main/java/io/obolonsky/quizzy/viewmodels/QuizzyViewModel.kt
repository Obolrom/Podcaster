package io.obolonsky.quizzy.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import arrow.core.raise.option
import arrow.core.recover
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.NO_ID
import io.obolonsky.quizzy.data.*
import io.obolonsky.quizzy.redux.*
import io.obolonsky.quizzy.redux.OperationType.*
import io.obolonsky.quizzy.redux.UiElementTypes.*
import io.obolonsky.quizzy.repositories.QuizOutputRepository
import io.obolonsky.quizzy.ui.components.*
import io.obolonsky.quizzy.usecases.GetLocalizationsUseCase
import io.obolonsky.quizzy.usecases.GetTemplateUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.SimpleContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.util.*

@Suppress("unused_parameter")
class QuizzyViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted private val quizId: UUID,
    private val getLocalizationsUseCase: GetLocalizationsUseCase,
    private val getTemplateUseCase: GetTemplateUseCase,
    private val quizOutputRepository: QuizOutputRepository,
) : ViewModel(), ContainerHost<QuizScreenState, QuizScreenSideEffect> {

    override val container: Container<QuizScreenState, QuizScreenSideEffect> = container(
        initialState = QuizScreenState(
            title = null,
        )
    )

    init {
//        loadTemplate()
        loadTemplateNew()
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
            } else if (component is MultiselectUiElement
                && action is MultiselectSelectToggleAction
                && changedId == component.id) {
                val newSelectedIds = if (component.selectedIds.contains(action.selectedId)) {
                    component.selectedIds.toMutableSet().apply { remove(action.selectedId) }
                } else {
                    component.selectedIds.toMutableSet().apply { add(action.selectedId) }
                }
                component.copy(selectedIds = newSelectedIds)
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
            } else if (component is RowUiElement
                && component.subcomponents.find { it.id == changedId } is RadioGroupUiElement
                && action is SelectRadioButtonAction) {
                val reducedSubcomponents = component.subcomponents
                    .map { uiElement ->
                        if (uiElement.id == changedId && uiElement is RadioGroupUiElement)
                            uiElement.copy(selectedId = action.selectedButtonId)
                        else
                            uiElement
                    }
                component.copy(subcomponents = reducedSubcomponents)
            } else if (component is RowUiElement
                && component.subcomponents.find { it.id == changedId } is CheckBoxUiElement
                && action is ToggleCheckBoxAction) {
                val reducedSubcomponents = component.subcomponents
                    .map { uiElement ->
                        if (uiElement.id == changedId && uiElement is CheckBoxUiElement)
                            uiElement.copy(isChecked = action.isChecked)
                        else
                            uiElement
                    }
                component.copy(subcomponents = reducedSubcomponents)
            } else {
                component
            }
        })

        val triggered = reduced.uiElements
            ?.mapNotNull { component ->
                if (component is CheckBoxUiElement
                    && action is ToggleCheckBoxAction
                    && changedId == component.id) {
                    val trigger = reduced.triggers?.find { it.fieldId == changedId }
                    val condition = trigger?.conditions?.find { condition ->
                        reduced.uiElements
                            .asSequence()
                            .filterIsInstance<RowUiElement>()
                            .map(RowUiElement::subcomponents)
                            .flatten()
                            .plus(reduced.uiElements)
                            .toList()
                            .find {
                                it.id == condition.conditionFieldId
                                        && it is CheckBoxUiElement
                                        && condition.conditionType == ConditionType.EQUALS
                                        && condition.value.toString().toBoolean() == it.isChecked
                            } != null
                    }

                    if (condition != null) trigger else null
                } else if (component is RowUiElement) {
                    component.subcomponents.firstNotNullOfOrNull { subcomponent ->
                        if (subcomponent is CheckBoxUiElement
                            && action is ToggleCheckBoxAction
                            && changedId == subcomponent.id
                        ) {
                            val trigger = reduced.triggers?.find { it.fieldId == changedId }
                            val condition = trigger?.conditions?.find { condition ->
                                reduced.uiElements
                                    .asSequence()
                                    .filterIsInstance<RowUiElement>()
                                    .map(RowUiElement::subcomponents)
                                    .flatten()
                                    .plus(reduced.uiElements)
                                    .toList() // maybe unnecessary
                                    .find {
                                        it.id == condition.conditionFieldId
                                                && it is CheckBoxUiElement
                                                && condition.conditionType == ConditionType.EQUALS
                                                && condition.value.toString().toBoolean() == it.isChecked
                                    } != null
                            }

                            if (condition != null) trigger else null
                        } else {
                            null
                        }
                    }
                } else {
                    null
                }
            }

        val finalReduced = triggered
            .orEmpty()
            .fold(reduced) { state, trigger ->
                state.copy(uiElements = state.uiElements?.map { component ->
                    val operation = trigger.operations.find { component.id == it.fieldId }
                    if (component is InputUiElement && operation != null) {
                        when (operation.operationType) {
                            SET_VALUE -> {
                                component.copy(value = operation.value.toString())
                            }
                        }
                    } else if (component is RadioGroupUiElement && operation != null) {
                        when (operation.operationType) {
                            SET_VALUE -> {
                                component.copy(selectedId = operation.value.toString())
                            }
                        }
                    } else if (component is CheckBoxUiElement && operation != null) {
                        when (operation.operationType) {
                            SET_VALUE -> {
                                component.copy(isChecked = operation.value.toString().toBoolean())
                            }
                        }
                    } else if (component is RowUiElement) {
                        component.copy(subcomponents = component.subcomponents.map { subcomponent ->
                            val subOperation = trigger.operations
                                .find { subcomponent.id == it.fieldId }

                            if (subcomponent is InputUiElement && subOperation != null) {
                                when (subOperation.operationType) {
                                    SET_VALUE -> {
                                        subcomponent.copy(value = subOperation.value.toString())
                                    }
                                }
                            } else if (subcomponent is RadioGroupUiElement && subOperation != null) {
                                when (subOperation.operationType) {
                                    SET_VALUE -> {
                                        subcomponent.copy(selectedId = subOperation.value.toString())
                                    }
                                }
                            } else if (subcomponent is CheckBoxUiElement && subOperation != null) {
                                when (subOperation.operationType) {
                                    SET_VALUE -> {
                                        subcomponent.copy(isChecked = subOperation.value.toString().toBoolean())
                                    }
                                }
                            } else {
                                subcomponent
                            }
                        })
                    } else {
                        component
                    }
                })
            }

        reduce { finalReduced }
    }

    fun submit() = intent {
        val checkBoxData = state.uiElements
            ?.filterIsInstance<CheckBoxUiElement>()
            ?.fold(mutableMapOf<String, Boolean>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.isChecked) }
            }
            .orEmpty()

        val checkBoxSubfieldsData = state.uiElements
            ?.asSequence()
            ?.filterIsInstance<RowUiElement>()
            ?.map { it.subcomponents }
            ?.flatten()
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

        val radioSubfieldsData = state.uiElements
            ?.asSequence()
            ?.filterIsInstance<RowUiElement>()
            ?.map { it.subcomponents }
            ?.flatten()
            ?.filterIsInstance<RadioGroupUiElement>()
            ?.filter { it.selectedId != NO_ID }
            ?.fold(mutableMapOf<String, String>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.selectedId) }
            }
            .orEmpty()

        val multiselectData = state.uiElements
            ?.filterIsInstance<MultiselectUiElement>()
            ?.filter { it.selectedIds.isNotEmpty() }
            ?.fold(mutableMapOf<String, Set<String>>()) { output, uiData ->
                output.apply { put(uiData.id, uiData.selectedIds) }
            }
            .orEmpty()

        val quizOutput = QuizOutput(
            id = UUID.randomUUID(),
            inputs = inputData + inputSubfieldsData,
            checkBoxes = checkBoxData + checkBoxSubfieldsData,
            radioGroups = radioData + radioSubfieldsData,
            multiselect = multiselectData,
        )

        Timber.d("quizOutput $quizOutput")

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

        if (!quizOutput.allKeys.containsAll(requiredFields)) {
            postSideEffect(QuizScreenSideEffect.NotAllRequiredFieldsAreFilled)
        } else {
            quizOutputRepository.saveQuiz(quizOutput)
                .collect()
        }
    }

    private fun loadTemplateNew() = intent {
        option {
            val template = getTemplateUseCase.getTemplate("templates/first_quiz.json").bind()
            val localization = getLocalizationsUseCase.getLocalization().bind()
            val saved = quizOutputRepository.getOptionQuiz(quizId)
                .recover {
                    QuizOutput(
                        id = quizId,
                        inputs = emptyMap(),
                        checkBoxes = emptyMap(),
                        radioGroups = emptyMap(),
                        multiselect = emptyMap(),
                    )
                }
                .bind()
            Triple(template, localization, saved)
        }
            .onSome { (template, localization, saved) ->
                reduce { reduceQuiz(template, localization, saved) }
            }
    }

    private fun loadTemplate() = intent {
        getTemplateUseCase.get("templates/first_quiz.json")
            .combine(getLocalizationsUseCase.get()) { template, localizations ->
                template to localizations
            }
            .combine(quizOutputRepository.getQuiz(quizId)) { (template, localizations), saved ->
                Triple(
                    first = template,
                    second = localizations,
                    third = saved ?: QuizOutput(
                        id = quizId,
                        inputs = emptyMap(),
                        checkBoxes = emptyMap(),
                        radioGroups = emptyMap(),
                        multiselect = emptyMap(),
                    ),
                )
            }
            .onEach { (template, localization, saved) ->
                reduce { reduceQuiz(template, localization, saved) }
            }
            .collect()
    }

    private fun SimpleContext<QuizScreenState>.reduceQuiz(
        template: QuizTemplate,
        localization: Localization,
        saved: QuizOutput,
    ): QuizScreenState {
        return state.copy(
            uiElements = template.fields.map { field ->
                when (field.type) {
                    TEXT_LABEL -> {
                        TextLabelUiElement(
                            type = field.type,
                            id = field.type.name,
                            label = localization.getString(field.labelKey),
                            weight = field.weight,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                    CHECKBOX -> {
                        CheckBoxUiElement(
                            id = field.id,
                            type = field.type,
                            label = localization.getString(field.labelKey),
                            isChecked = saved.checkBoxes[field.id] ?: false,
                            weight = field.weight,
                            required = field.required,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                    INPUT -> {
                        InputUiElement(
                            id = field.id,
                            type = field.type,
                            label = localization.getString(field.labelKey),
                            value = saved.inputs[field.id] ?: "",
                            weight = field.weight,
                            required = field.required,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                    ROW -> {
                        RowUiElement(
                            id = field.id,
                            type = field.type,
                            label = localization.getString(field.labelKey),
                            subcomponents = field.subfields.map { subfield ->
                                when (subfield.type) {
                                    TEXT_LABEL -> {
                                        TextLabelUiElement(
                                            type = subfield.type,
                                            id = subfield.type.name,
                                            label = localization.getString(subfield.labelKey),
                                            weight = subfield.weight,
                                            paddings = subfield.paddings?.let { paddings ->
                                                Paddings(
                                                    start = paddings.start,
                                                    end = paddings.end,
                                                    top = paddings.top,
                                                    bottom = paddings.bottom,
                                                )
                                            },
                                        )
                                    }
                                    CHECKBOX -> {
                                        CheckBoxUiElement(
                                            id = subfield.id,
                                            type = subfield.type,
                                            label = localization.getString(subfield.labelKey),
                                            isChecked = saved.checkBoxes[subfield.id] ?: false,
                                            weight = subfield.weight,
                                            required = subfield.required,
                                            paddings = subfield.paddings?.let { paddings ->
                                                Paddings(
                                                    start = paddings.start,
                                                    end = paddings.end,
                                                    top = paddings.top,
                                                    bottom = paddings.bottom,
                                                )
                                            },
                                        )
                                    }
                                    INPUT -> {
                                        InputUiElement(
                                            id = subfield.id,
                                            type = subfield.type,
                                            label = localization.getString(subfield.labelKey),
                                            value = saved.inputs[subfield.id] ?: "",
                                            weight = subfield.weight,
                                            required = subfield.required,
                                            paddings = subfield.paddings?.let { paddings ->
                                                Paddings(
                                                    start = paddings.start,
                                                    end = paddings.end,
                                                    top = paddings.top,
                                                    bottom = paddings.bottom,
                                                )
                                            },
                                        )
                                    }
                                    ROW -> error("Not supported")
                                    RADIO -> {
                                        RadioGroupUiElement(
                                            id = subfield.id,
                                            type = subfield.type,
                                            label = localization.getString(subfield.labelKey),
                                            weight = subfield.weight,
                                            values = subfield.values?.map { radioValue ->
                                                RadioGroupUiElement.RadioButtonUiElement(
                                                    id = radioValue.id,
                                                    label = localization.getString(radioValue.labelKey),
                                                )
                                            }.orEmpty(),
                                            selectedId = saved.radioGroups[subfield.id] ?: NO_ID,
                                            required = subfield.required,
                                            paddings = subfield.paddings?.let { paddings ->
                                                Paddings(
                                                    start = paddings.start,
                                                    end = paddings.end,
                                                    top = paddings.top,
                                                    bottom = paddings.bottom,
                                                )
                                            },
                                        )
                                    }
                                    MULTISELECT -> error("Not supported")
                                }
                            },
                            weight = field.weight,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                    RADIO -> {
                        RadioGroupUiElement(
                            id = field.id,
                            type = field.type,
                            label = localization.getString(field.labelKey),
                            weight = field.weight,
                            values = field.values?.map { radioValue ->
                                RadioGroupUiElement.RadioButtonUiElement(
                                    id = radioValue.id,
                                    label = localization.getString(radioValue.labelKey),
                                )
                            }.orEmpty(),
                            selectedId = saved.radioGroups[field.id] ?: NO_ID,
                            required = field.required,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                    MULTISELECT -> {
                        MultiselectUiElement(
                            id = field.id,
                            type = field.type,
                            label = localization.getString(field.labelKey),
                            weight = field.weight,
                            values = field.values?.map { radioValue ->
                                MultiselectUiElement.SelectElement(
                                    id = radioValue.id,
                                    label = localization.getString(radioValue.labelKey),
                                )
                            }.orEmpty(),
                            selectedIds = saved.multiselect[field.id] ?: emptySet(),
                            required = field.required,
                            paddings = field.paddings?.let { paddings ->
                                Paddings(
                                    start = paddings.start,
                                    end = paddings.end,
                                    top = paddings.top,
                                    bottom = paddings.bottom,
                                )
                            },
                        )
                    }
                }
            },
            triggers = template.triggers,
        )
    }

    @AssistedFactory
    interface Factory {

        fun create(
            savedStateHandle: SavedStateHandle,
            quizId: UUID,
        ): QuizzyViewModel
    }
}