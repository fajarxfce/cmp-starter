package com.fajar.kmp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull

class NetworkDependencyGuardTest {
    @Test
    fun ktor_client_dependencies_are_available() {
        val client = HttpClient(MockEngine { respond("{}", HttpStatusCode.OK) }) {
            install(ContentNegotiation) { json(Json) }
            install(Logging)
            install(Auth)
        }

        assertNotNull(client)
        client.close()
    }
}
