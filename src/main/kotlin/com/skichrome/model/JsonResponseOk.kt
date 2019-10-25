package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonResponseOk(
        val status: String = "OK",
        var last_updated: String? = null,
        val lang: String = "FR",
        val result: Any? = null
)
{
    init
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        last_updated = sdf.format(System.currentTimeMillis())
    }
}