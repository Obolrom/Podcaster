package io.obolonsky.quizzy.repositories

import android.content.Context
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.quizzy.data.QuizOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class QuizOutputRepository @Inject constructor(
    context: Context,
    private val jsonConverter: JsonConverter,
    private val coroutineSchedulers: CoroutineSchedulers,
) {

    private val quizDirectory = context.getDir("quizzy", Context.MODE_PRIVATE)

    fun saveQuiz(quizOutput: QuizOutput): Flow<Unit> {
        return flow {
            val serializedQuiz = jsonConverter.toJson(quizOutput)
            val quizFile = File(quizDirectory.absolutePath, quizOutput.id.toString() + ".json")
            quizFile.writeText(serializedQuiz)
            emit(Unit)
        }
            .flowOn(coroutineSchedulers.io)
    }
}