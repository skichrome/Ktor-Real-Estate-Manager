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
                    li { +"/init : database initialisation, to be triggered at first load of this application" }
                    li { +"/currency-conversion-rate" }
                    li {
                        +"/all-realty"
                        ul {
                            li {
                                +"/agent-id={id}"
                                ul {
                                    li { +"GET" }
                                }
                            }
                            li { +"POST" }
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
                            li {
                                +"/agent-id={id}"
                                ul {
                                    li { +"GET" }
                                }
                            }
                            li { +"POST" }
                        }
                    }
                    li {
                        +"/media-references"
                        ul {
                            li {
                                +"/upload : POST : upload a media to the server in MultiPart format, with its associated needed informations."
                            }
                            li { +"/agent-id={agent} : GET : get all medias from one agent" }
                            li { +"/{mediaRefId} : GET : get an url of specified media id." }
                            li { +"/delete-delta : POST : Compare the list in parameter and delete media that have been deleted on client, and return a list of id that aren't available on server (list of media to upload)" }
                        }
                    }
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
                }
            }
        }
    }
}