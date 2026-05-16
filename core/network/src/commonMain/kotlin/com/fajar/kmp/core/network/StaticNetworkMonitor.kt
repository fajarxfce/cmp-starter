package com.fajar.kmp.core.network

class StaticNetworkMonitor(private val online: Boolean) : NetworkMonitor {
    override suspend fun isOnline(): Boolean = online
}
