package com.fajar.kmp.core.network

import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthLoginResponse
import com.fajar.kmp.core.network.data.PosApiJson
import com.fajar.kmp.core.network.data.PosApiPaths
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class KtorApiClientTest {
    private val requestJson = Json { encodeDefaults = true }

    @Test
    fun login_success_returns_response_body_for_parsing() = runTest {
        val client = testClient(
            engine = MockEngine { request ->
                assertEquals(PosApiPaths.authLogin, request.url.encodedPath)
                assertEquals(io.ktor.http.HttpMethod.Post, request.method)
                respond(
                    content = """{"success":true,"data":{"accessToken":"token-123"}}""",
                    status = HttpStatusCode.OK,
                )
            },
        )

        val result = client.execute(
            ApiRequest(
                path = PosApiPaths.authLogin,
                method = HttpMethod.Post,
                body = requestJson.encodeToString(
                    AuthLoginRequest.serializer(),
                    AuthLoginRequest("admin@posgg.dev", "adminpass123"),
                ),
            ),
        )

        val response = result.successValue()
        val login = PosApiJson.tolerant.decodeFromString(AuthLoginResponse.serializer(), response.body)
        assertEquals(200, response.statusCode)
        assertEquals("token-123", login.data?.accessToken)
    }

    @Test
    fun offline_monitor_returns_offline_before_engine_runs() = runTest {
        val client = testClient(
            online = false,
            engine = MockEngine { fail("MockEngine should not be reached while offline") },
        )

        val result = client.execute(ApiRequest(PosApiPaths.authLogin, HttpMethod.Post))

        assertEquals(NetworkResult.Failure(NetworkError.Offline), result)
    }

    @Test
    fun unauthorized_status_maps_to_unauthorized() = runTest {
        val client = testClient(engine = MockEngine { respondError(HttpStatusCode.Unauthorized, "Unauthorized") })

        val result = client.execute(ApiRequest(PosApiPaths.adminUsers, HttpMethod.Get))

        assertEquals(NetworkResult.Failure(NetworkError.Unauthorized), result)
    }

    @Test
    fun server_error_status_maps_to_server_error() = runTest {
        val client = testClient(engine = MockEngine { respondError(HttpStatusCode.InternalServerError, "Server down") })

        val result = client.execute(ApiRequest(PosApiPaths.adminStats, HttpMethod.Get))

        assertEquals(NetworkResult.Failure(NetworkError.Server(500, "Server down")), result)
    }

    @Test
    fun json_body_is_sent_with_application_json_content_type() = runTest {
        val body = requestJson.encodeToString(
            AuthLoginRequest.serializer(),
            AuthLoginRequest("admin@posgg.dev", "adminpass123"),
        )
        val client = testClient(
            engine = MockEngine { request ->
                val content = request.body as TextContent
                assertEquals(body, content.text)
                assertEquals(ContentType.Application.Json, content.contentType)
                respond("{}", HttpStatusCode.OK)
            },
        )

        client.execute(ApiRequest(PosApiPaths.authLogin, HttpMethod.Post, body = body)).successValue()
    }

    @Test
    fun bearer_token_provider_injects_authorization_header() = runTest {
        val client = testClient(
            bearerTokenProvider = BearerTokenProvider { "token-abc" },
            engine = MockEngine { request ->
                assertEquals("Bearer token-abc", request.headers[HttpHeaders.Authorization])
                respond("{}", HttpStatusCode.OK)
            },
        )

        client.execute(ApiRequest(PosApiPaths.adminUsers, HttpMethod.Get)).successValue()
    }

    @Test
    fun placeholder_base_url_fails_clearly() {
        val error = assertFailsWith<IllegalArgumentException> {
            testClient(
                baseUrl = "REPLACE_WITH_API_BASE_URL",
                engine = MockEngine { respond("{}", HttpStatusCode.OK) },
            )
        }

        assertTrue(error.message?.contains("placeholder") == true)
    }

    @Test
    fun cancellation_is_rethrown_instead_of_mapped_to_unknown() = runTest {
        val client = testClient(
            engine = MockEngine { throw CancellationException("cancelled") },
        )

        assertFailsWith<CancellationException> {
            client.execute(ApiRequest(PosApiPaths.adminUsers, HttpMethod.Get))
        }
    }

    private fun testClient(
        engine: MockEngine,
        baseUrl: String = "https://pos.test",
        online: Boolean = true,
        bearerTokenProvider: BearerTokenProvider = BearerTokenProvider { null },
    ): KtorApiClient = KtorApiClient(
        baseUrl = baseUrl,
        networkMonitor = StaticNetworkMonitor(online),
        bearerTokenProvider = bearerTokenProvider,
        engine = engine,
    )

    private fun NetworkResult<ApiResponse>.successValue(): ApiResponse = when (this) {
        is NetworkResult.Success -> value
        is NetworkResult.Failure -> fail("Expected success but was $this")
    }
}
