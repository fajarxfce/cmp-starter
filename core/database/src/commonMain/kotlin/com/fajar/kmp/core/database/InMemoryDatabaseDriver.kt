package com.fajar.kmp.core.database

class InMemoryDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(databaseName: String): DatabaseDriver = InMemoryDatabaseDriver(databaseName)
}

class InMemoryDatabaseDriver(val databaseName: String) : DatabaseDriver {
    override suspend fun transaction(block: suspend DatabaseTransaction.() -> Unit) {
        block(InMemoryDatabaseTransaction)
    }
}

data object InMemoryDatabaseTransaction : DatabaseTransaction
