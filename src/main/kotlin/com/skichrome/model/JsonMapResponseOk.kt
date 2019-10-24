package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonMapResponseOk(
        val status: String = "OK",
        var last_updated: String? = null,
        val lang: String = "FR",
        val result: Map<String, Float>? = null
)
{
    init
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        last_updated = sdf.format(System.currentTimeMillis())
    }
}