package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonListResponseOk(
        val status: String = "OK",
        var last_updated: String? = null,
        val lang: String = "FR",
        var num_results: Int = 0,
        val result: List<*>? = null
)
{
    init
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        last_updated = sdf.format(System.currentTimeMillis())
        num_results = result?.size ?: -1
    }
}