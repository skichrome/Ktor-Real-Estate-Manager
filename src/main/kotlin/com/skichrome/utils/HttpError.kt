package com.skichrome.utils

import io.ktor.http.HttpStatusCode

data class HttpError(val request: String,
                     val message: String,
                     val code: HttpStatusCode,
                     val cause: Throwable? = null)