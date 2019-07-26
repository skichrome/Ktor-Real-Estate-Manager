package com.skichrome

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.html.*

fun Application.main()
{
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing)
    {
        route("/debug")
        {
            get("/")
            {
                call.respondText("Hello world", ContentType.Text.Html)
            }
            get("/{field}")
            {
                val args = call.parameters["field"] ?: ""
                call.respondText("unsupported value entered : $args")
            }
            get("/html")
            {
                call.respondHtml {
                    head { title { +"Html response test" } }
                    body {
                        h1("title") { +"Hello World" }
                        p { +"An hello world test with Ktor." }
                    }
                }
            }
        }
    }
}