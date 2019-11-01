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

            // --------- Help page about available routes ---------

            route("/real-estate")
            {
                get("/") {
                    call.respondHtml {
                        body {
                            h1 { +"Available Routes" }
                            ul {
                                li { +"/currency-conversion-rate" }
                                li { +"/all-realty-types : GET" }
                                li { +"/all-poi : GET" }
                                li { +"/all-agents/agent={id} : GET" }
                                li { +"/all-agents : GET / POST ; example of content to send : | {\"agentId\": \"1\", \"name\":\"Boris\",\"lastUpdate\":\"25/10/2019 10:06:00\"} |" }
                                li { +"/all-poi-realty : GET / POST" }
                                li { +"/all-media-references : GET / POST" }
                                li { +"/all-realty : GET / POST" }
                            }
                        }
                    }
                }

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
                    get("/") {
                        val response = DbFactory.getAllRealty()
                        call.respond(JsonListResponseOk(result = response))
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
                    get("/agent={id}")
                    {
                        val agentId = call.parameters["id"]?.toLongOrNull()
                        agentId?.let { agentIdNotNull ->
                            val response = DbFactory.getAgentById(agentIdNotNull)

                            response?.let {
                                call.respond(JsonResponseOk(result = it))
                            } ?: call.respond(HttpStatusCode.NotFound, "Error when trying to get this agent.")
                        } ?: call.respond(HttpStatusCode.BadRequest, "You must enter a valid number for this request")
                    }
                    post("/") {
                        val agentData = call.receive<Array<AgentData>>().toList()
                        DbFactory.insertAgentList(agentData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/all-poi-realty")
                {
                    get("/") {
                        val response = DbFactory.getAllPoiRealty()
                        call.respond(JsonListResponseOk(result = response))
                    }
                    post("/") {
                        val poiRealtyData = call.receive<Array<PoiRealtyData>>().toList()
                        DbFactory.insertPoiRealtyList(poiRealtyData)
                        call.respond(mapOf("OK" to true))
                    }
                }

                route("/all-media-references")
                {
                    get("/") {
                        val response = DbFactory.getAllMediaReference()
                        call.respond(JsonListResponseOk(result = response))
                    }
                    post("/") {
                        val mediaRefData = call.receive<Array<MediaReferenceData>>().toList()
                        DbFactory.insertMediaReferenceList(mediaRefData)
                        call.respond(mapOf("OK" to true))
                    }
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