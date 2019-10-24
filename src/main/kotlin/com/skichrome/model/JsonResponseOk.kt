package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonResponseOk(
        val status: String = "OK",
        var last_updated: String? = null,
        val lang: String = "FR",
        var numResults: Int = 0,
        val result: List<*>? = null
)
{
    private val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

    init
    {
        numResults = result?.size ?: -1
        last_updated = sdf.format(System.currentTimeMillis())
    }
}