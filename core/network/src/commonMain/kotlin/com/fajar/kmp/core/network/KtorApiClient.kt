package com.fajar.kmp.core.network

import com.fajar.kmp.core.network.data.PosApiJson
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import kotlin.coroutines.cancellation.CancellationException

fun interface BearerTokenProvider {
    suspend fun bearerToken(): String?
}

class KtorApiClient private constructor(
    private val baseUrl: String,
    private val networkMonitor: NetworkMonitor,
    private val bearerTokenProvider: BearerTokenProvider,
    private val errorMapper: NetworkErrorMapper,
    private val client: HttpClient,
) : ApiClient {
    constructor(
        baseUrl: String,
        networkMonitor: NetworkMonitor,
        bearerTokenProvider: BearerTokenProvider = BearerTokenProvider { null },
        errorMapper: NetworkErrorMapper = NetworkErrorMapper(),
        requestTimeoutMillis: Long = DefaultRequestTimeoutMillis,
    ) : this(
        baseUrl = normalizeBaseUrl(baseUrl),
        networkMonitor = networkMonitor,
        bearerTokenProvider = bearerTokenProvider,
        errorMapper = errorMapper,
        client = buildHttpClient(platformHttpClientEngineFactory(), requestTimeoutMillis),
    )

    internal constructor(
        baseUrl: String,
        networkMonitor: NetworkMonitor,
        bearerTokenProvider: BearerTokenProvider = BearerTokenProvider { null },
        errorMapper: NetworkErrorMapper = NetworkErrorMapper(),
        requestTimeoutMillis: Long = DefaultRequestTimeoutMillis,
        engine: HttpClientEngine,
    ) : this(
        baseUrl = normalizeBaseUrl(baseUrl),
        networkMonitor = networkMonitor,
        bearerTokenProvider = bearerTokenProvider,
        errorMapper = errorMapper,
        client = buildHttpClient(engine, requestTimeoutMillis),
    )

    override suspend fun execute(request: ApiRequest): NetworkResult<ApiResponse> {
        if (!networkMonitor.isOnline()) return NetworkResult.Failure(NetworkError.Offline)

        return try {
            val response = client.request(baseUrl + request.path.withLeadingSlash()) {
                method = request.method.toKtorMethod()
                request.headers.forEach { (name, value) -> header(name, value) }
                request.idempotencyKey?.let { header(IdempotencyKeyHeader, it) }
                addBearerTokenIfMissing(request.headers)
                request.body?.let { setBody(TextContent(it, ContentType.Application.Json)) }
            }
            val body = response.bodyAsText()
            val statusCode = response.status.value

            if (statusCode in 200..299) {
                NetworkResult.Success(
                    ApiResponse(
                        statusCode = statusCode,
                        body = body,
                        headers = response.headers.entries().associate { (name, values) -> name to values.joinToString(",") },
                    ),
                )
            } else {
                NetworkResult.Failure(errorMapper.mapStatus(statusCode, body))
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            NetworkResult.Failure(NetworkError.Unknown(exception.message ?: exception.toString()))
        }
    }

    private suspend fun HttpRequestBuilder.addBearerTokenIfMissing(headers: Map<String, String>) {
        val hasAuthorization = headers.keys.any { it.equals(HttpHeaders.Authorization, ignoreCase = true) }
        if (!hasAuthorization) {
            bearerTokenProvider.bearerToken()
                ?.takeIf { it.isNotBlank() }
                ?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }
}

private const val DefaultRequestTimeoutMillis = 15_000L
private const val IdempotencyKeyHeader = "Idempotency-Key"

private fun buildHttpClient(engineFactory: HttpClientEngineFactory<*>, requestTimeoutMillis: Long): HttpClient =
    HttpClient(engineFactory) {
        configureKtorClient(requestTimeoutMillis)
    }

private fun buildHttpClient(engine: HttpClientEngine, requestTimeoutMillis: Long): HttpClient =
    HttpClient(engine) {
        configureKtorClient(requestTimeoutMillis)
    }

private fun HttpClientConfig<out HttpClientEngineConfig>.configureKtorClient(requestTimeoutMillis: Long) {
    expectSuccess = false
    install(ContentNegotiation) {
        json(PosApiJson.tolerant)
    }
    install(Logging) {
        level = LogLevel.HEADERS
        sanitizeHeader { header -> header.equals(HttpHeaders.Authorization, ignoreCase = true) }
    }
    install(HttpTimeout) {
        this.requestTimeoutMillis = requestTimeoutMillis
    }
}

private fun normalizeBaseUrl(baseUrl: String): String {
    val trimmed = baseUrl.trim().trimEnd('/')
    require(trimmed.isNotBlank()) { "Base URL must not be blank" }
    require(!trimmed.contains("REPLACE_WITH_")) { "Base URL is still a placeholder: $baseUrl" }
    require(trimmed.startsWith("https://") || trimmed.startsWith("http://")) {
        "Base URL must be absolute: $baseUrl"
    }
    Url(trimmed)
    return trimmed
}

private fun String.withLeadingSlash(): String = if (startsWith('/')) this else "/$this"

private fun HttpMethod.toKtorMethod(): io.ktor.http.HttpMethod = when (this) {
    HttpMethod.Get -> io.ktor.http.HttpMethod.Get
    HttpMethod.Post -> io.ktor.http.HttpMethod.Post
    HttpMethod.Put -> io.ktor.http.HttpMethod.Put
    HttpMethod.Patch -> io.ktor.http.HttpMethod.Patch
    HttpMethod.Delete -> io.ktor.http.HttpMethod.Delete
}
