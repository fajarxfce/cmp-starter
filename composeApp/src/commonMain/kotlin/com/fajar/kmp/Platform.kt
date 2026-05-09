package com.fajar.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform