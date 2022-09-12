package io.obolonsky.storage.database.utils

interface TransactionManager {

    suspend fun runTransaction(transaction: () -> Unit)
}