package com.skichrome

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.html.*

fun Application.main()
{
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation)
    {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("    ", "\n"))
            })
        }
    }
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
        route("/real-estate")
        {
            get("/")
            {
                call.respondHtml {
                    head { title { +"Real Estate List" } }
                    body {
                        h1 { +"Real Estate List" }
                        p { +"This is an amazing list of all real estates available in database" }
                        ul {
                            li { +"Penthouse" }
                            li { +"House" }
                            li { +"duplex" }
                            li { +"lofts" }
                        }
                    }
                }
            }
            get("/parse")
            {
                call.respondText(XmlParser.getDataFromXml())
            }
        }
    }
}