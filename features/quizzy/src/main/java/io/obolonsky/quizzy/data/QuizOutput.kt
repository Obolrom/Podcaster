package io.obolonsky.quizzy.data

import java.util.UUID

/**
 * Model to collect all the data fields from the form
 */
data class QuizOutput(
    val id: UUID,
    val inputs: Map<String, String>,
    val checkBoxes: Map<String, Boolean>,
    val radioGroups: Map<String, String>,
    val multiselect: Map<String, Set<String>>
) {

    val allKeys: Set<String>
        get() = inputs.keys + checkBoxes.keys + radioGroups.keys + multiselect.keys
}