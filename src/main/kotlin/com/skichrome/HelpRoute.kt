package com.skichrome

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul

fun Route.help()
{
    get("/") {
        call.respondHtml {
            body {
                h1 { +"Available Routes" }
                ul {
                    li { +"/currency-conversion-rate" }
                    li {
                        +"/all-realty-types"
                        ul {
                            li { +"GET" }
                        }
                    }
                    li {
                        +"/all-poi"
                        ul {
                            li { +"GET" }
                        }
                    }
                    li {
                        +"/all-agents/agent={id}"
                        ul {
                            li { +"GET" }
                        }
                    }
                    li {
                        +"/all-agents"
                        ul {
                            li { +"GET" }
                            li { +"POST" }
                        }
                    }
                    li {
                        +"/all-poi-realty"
                        ul {
                            li { +"GET" }
                            li { +"POST" }
                        }
                    }
                    li {
                        +"/media-references"
                        ul {
                            li { +"GET" }
                            li { +"POST" }
                        }
                    }
                    li {
                        +"/all-realty"
                        ul {
                            li { +"GET" }
                            li { +"POST" }
                        }
                    }
                }
            }
        }
    }
}