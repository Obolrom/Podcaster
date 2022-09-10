package io.obolonsky.repository.database

interface TransactionManager {

    suspend fun runTransaction(transaction: () -> Unit)
}