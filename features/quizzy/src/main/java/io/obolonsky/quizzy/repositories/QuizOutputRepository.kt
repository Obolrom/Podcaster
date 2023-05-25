package io.obolonsky.quizzy.repositories

import android.content.Context
import arrow.core.*
import arrow.core.raise.ensureNotNull
import arrow.core.raise.option
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.quizzy.data.QuizOutput
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.UUID
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

    fun getOptionQuiz(id: UUID): Option<QuizOutput> = option {
        val quizFile = quizDirectory
            .walkTopDown()
            .filter { it.isFile && it.name == "$id.json" }
            .firstOrNull()
        ensureNotNull(quizFile)
    }
        .map { quizFile ->
            jsonConverter.fromJson(quizFile.readText(), QuizOutput::class.java)
        }

    fun getQuiz(id: UUID): Flow<QuizOutput?> {
        return flow {
            val quizFile = quizDirectory
                .walkTopDown()
                .filter { it.isFile && it.name == "$id.json" }
                .firstOrNull()
            emit(quizFile)
        }
            .mapNotNull { quizFile ->
                jsonConverter.fromJson(quizFile?.readText(), QuizOutput::class.java)
            }
    }

    fun getSavedQuizzes(): Flow<List<QuizOutput>> {
        return flow {
            val quizzesFiles = quizDirectory
                .walkTopDown()
                .filter { it.isFile }
                .toList()
            emit(quizzesFiles)
        }
            .map { quizzesFiles ->
                quizzesFiles.map { serializedQuiz ->
                    jsonConverter.fromJson(serializedQuiz.readText(), QuizOutput::class.java)
                }
            }
    }
}