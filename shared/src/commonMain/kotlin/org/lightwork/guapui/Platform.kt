package org.lightwork.guapui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform