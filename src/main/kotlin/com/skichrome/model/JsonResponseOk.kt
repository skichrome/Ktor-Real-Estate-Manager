package com.skichrome.model

import java.text.SimpleDateFormat

data class JsonResponseOk(
        val status: String = "OK",
        var system_date: String? = null,
        val lang: String = "FR",
        val result: Any? = null
)
{
    init
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        system_date = sdf.format(System.currentTimeMillis())
    }
}