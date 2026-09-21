package org.vaulture.project.core.domain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform