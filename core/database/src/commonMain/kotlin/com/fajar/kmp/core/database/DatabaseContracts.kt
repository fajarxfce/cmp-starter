package com.fajar.kmp.core.database

interface DatabaseDriverFactory {
    fun createDriver(databaseName: String): DatabaseDriver
}

interface DatabaseDriver {
    suspend fun transaction(block: suspend DatabaseTransaction.() -> Unit)
}

interface DatabaseTransaction

data class DatabaseConfig(
    val name: String,
    val schemaVersion: Int,
    val destructiveMigrationAllowed: Boolean = false,
)

interface LocalEntityMapper<Entity, Domain> {
    fun toDomain(entity: Entity): Domain
    fun toEntity(domain: Domain): Entity
}

data class PendingSyncOperation(
    val id: String,
    val aggregateId: String,
    val type: SyncOperationType,
    val payload: String,
    val idempotencyKey: String,
    val retryCount: Int,
    val lastError: String?,
)

enum class SyncOperationType { Create, Update, Delete }
