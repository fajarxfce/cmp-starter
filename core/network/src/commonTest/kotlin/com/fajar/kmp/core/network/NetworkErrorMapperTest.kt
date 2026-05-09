package com.fajar.kmp.core.network

import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkErrorMapperTest {
    @Test
    fun unauthorized_status_maps_to_unauthorized_error() {
        assertEquals(NetworkError.Unauthorized, NetworkErrorMapper().mapStatus(401, "Unauthorized"))
    }
}
