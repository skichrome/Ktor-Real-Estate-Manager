package com.skichrome

import com.skichrome.model.*
import com.skichrome.utils.HttpError
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
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
        gson {
            setPrettyPrinting()
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpError(
                    code = HttpStatusCode.InternalServerError,
                    request = call.request.local.uri,
                    message = cause.toString(),
                    cause = cause
            ))
        }
    }
    install(Routing) {
        static {
            resource(resource = "favicon.ico", remotePath = "favicon.ico")
        }
        route("/") {
            get("/") {
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
            get("/init") {
                val response = DbFactory.initDb()
                call.respondText(response, ContentType.Text.Html)
            }
        }
        route("/debug") {
            get("/") {
                call.respondText("Hello world", ContentType.Text.Html)
            }
            get("/{field}") {
                val args = call.parameters["field"] ?: ""
                call.respondText("unsupported value entered : $args")
            }
            get("/html") {
                call.respondHtml {
                    head { title { +"Html response test" } }
                    body {
                        h1("title") { +"Hello World" }
                        p { +"An hello world test with Ktor." }
                    }
                }
            }
        }
        route("/real-estate") {
            get("/") {
                call.respondHtml {
                    body {
                        h1 { +"Available Routes" }
                        ul {
                            li { +"/currency-conversion-rate" }
                            li { +"/realty-types" }
                            li { +"/all-poi" }
                            li { +"/agents" }
                            li { +"/agents/agent/{id}" }
                            li { +"/agents/agent (POST)" }
                        }
                    }
                }
            }

            // --------- Currency Conversion Rate ---------

            get("/currency-conversion-rate") {
                val currencyConversionValue = XmlParser.getDataFromXml()
                currencyConversionValue?.let {
                    val formattedResponse = JsonMapResponseOk(result = it)
                    call.respond(formattedResponse)
                } ?: call.respond(
                        HttpStatusCode.InternalServerError,
                        "Error when trying to send currency conversion rate"
                )
            }

            // --------- Get a List of elements ---------

            get("/realty-types") {
                val response = DbFactory.getAllRealtyTypes()
                call.respond(JsonListResponseOk(result = response))
            }
            get("/all-poi") {
                val response = DbFactory.getAllPoi()
                call.respond(JsonListResponseOk(result = response))
            }
            get("/agents") {
                val response = DbFactory.getAllAgents()
                call.respond(JsonListResponseOk(result = response))
            }

            // --------- Get elements by ID ---------

            get("/agents/agent/{id}")
            {
                val agentId = call.parameters["id"]?.toLongOrNull()
                agentId?.let { agentIdNotNull ->
                    val response = DbFactory.getAgentById(agentIdNotNull)

                    response?.let {
                        call.respond(JsonResponseOk(result = it))
                    } ?: call.respond(HttpStatusCode.NotFound, "Error when trying to get this agent.")
                } ?: call.respond(HttpStatusCode.BadRequest, "You must enter a valid number for this request")
            }

            // --------- Insert element ---------

            post("/agents/agent")
            {
                val agentData = call.receive<AgentData>()
                DbFactory.insertNewAgent(agentData)
                call.respond(mapOf("OK" to true))
            }
        }
    }
}