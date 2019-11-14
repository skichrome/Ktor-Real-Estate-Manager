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
            setDateFormat("dd/MM/yyyy hh:mm:ss")
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

        // --------- Main Entry Point ---------

        route("/")
        {
            // --------- Home Page ---------

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

            route("/real-estate")
            {
                // --------- Help page about available routes ---------

                help()

                // --------- Database Initialisation ---------

                get("/init") {
                    val response = DbFactory.initDb()
                    call.respondText(response, ContentType.Text.Html)
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

                // --------- Read / Write from API ---------

                route("/all-realty")
                {
                    get("/agent-id={agent}") {
                        val agent = call.parameters["agent"]?.toLongOrNull()
                        agent?.let {
                            val response = DbFactory.getAllRealtyFromAgent(agent)
                            call.respond(JsonListResponseOk(result = response))
                        }
                    }
                    post("/") {
                        val realtyData = call.receive<Array<RealtyData>>().toList()
                        DbFactory.insertRealtyList(realtyData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/all-agents")
                {
                    get("/") {
                        val response = DbFactory.getAllAgents()
                        call.respond(JsonListResponseOk(result = response))
                    }
                    post("/") {
                        val agentData = call.receive<Array<AgentData>>().toList()
                        DbFactory.insertAgentList(agentData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/agent") {
                    post("/update") {
                        val agentData = call.receive<AgentData>()
                        DbFactory.updateAgent(agentData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/all-poi-realty")
                {
                    get("/agent-id={agent}") {
                        call.parameters["agent"]?.toLongOrNull()?.let { agentId ->
                            val response = DbFactory.getAllPoiRealty(agentId)
                            call.respond(JsonListResponseOk(result = response))
                        }
                    }
                    post("/") {
                        val poiRealtyData = call.receive<Array<PoiRealtyData>>().toList()
                        DbFactory.insertPoiRealtyList(poiRealtyData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/media-references")
                {
                    mediaReference()
                }

                // --------- Read Only from API ---------

                route("/all-realty-types")
                {
                    get("/") {
                        val response = DbFactory.getAllRealtyTypes()
                        call.respond(JsonListResponseOk(result = response))
                    }
                }
                route("/all-poi")
                {
                    get("/") {
                        val response = DbFactory.getAllPoi()
                        call.respond(JsonListResponseOk(result = response))
                    }
                }
            }
        }
    }
}