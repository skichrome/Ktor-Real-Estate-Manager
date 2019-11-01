package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonListResponseOk(
        val status: String = "OK",
        var system_date: String? = null,
        val lang: String = "FR",
        var num_results: Int = 0,
        val result: List<*>? = null
)
{
    init
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        system_date = sdf.format(System.currentTimeMillis())
        num_results = result?.size ?: -1
    }
}