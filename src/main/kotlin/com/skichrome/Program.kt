package com.skichrome

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.skichrome.model.DbFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.html.*

/*
 * URL for html templates and css style
 * https://www.w3schools.com/w3css/default.asp
 */
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
        static {
            resource(resource = "favicon.ico", remotePath = "favicon.ico")
        }
        route("/")
        {
            get("/")
            {
                call.respondHtml {
                    head {
                        link(rel = "stylesheet", href = "https://www.w3schools.com/w3css/4/w3.css")
                    }
                    body {
                        header(classes = "w3-container w3-teal") {
                            h5 { +"Ktor Backend Server" }
                        }
                        footer(classes = "w3-container w3-teal w3-display-bottommiddle") {
                            p { +"Ktor backend server for mobile application." }
                        }
                    }
                }
            }
            get("/init")
            {
                val response = DbFactory.initDb()
                call.respondText(response, ContentType.Text.Html)
            }
        }
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