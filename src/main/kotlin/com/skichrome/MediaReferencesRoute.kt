package com.skichrome

import com.skichrome.model.DbFactory
import com.skichrome.model.JsonListResponseOk
import com.skichrome.model.MediaReferenceData
import io.ktor.application.call
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import java.io.File

fun Route.mediaReference()
{
    get("/agent-id={agent}") {
        call.parameters["agent"]?.toLongOrNull()?.let {
            val response = DbFactory.getAllMediaReference(it)
            call.respond(JsonListResponseOk(result = response))
        }
    }
    post("/") {
        val mediaRefData = call.receive<Array<MediaReferenceData>>().toList()
        DbFactory.insertMediaReferenceList(mediaRefData)
        call.respond(mapOf("OK" to true))
    }
    post("/delete-exist") {
        val androidAppMediaRef = call.receive<Array<Int>>().toList()
        DbFactory.deleteUnavailableMediaRef(androidAppMediaRef)
        call.respond(mapOf("OK" to true))
    }

    route("/upload") {
        upload()
    }
}

fun Route.upload()
{
    post("/") {
        val multipart = call.receiveMultipart()
        var title = ""
        var imgFile: File? = null
        multipart.forEachPart { part ->
            when (part)
            {
                is PartData.FormItem ->
                {
                    if (part.name == "title")
                        title = part.value
                }
                is PartData.FileItem ->
                {
                    part.originalFileName?.let {
                        val imgExt = File(it).extension
                        val file = File("/", "upload-${System.currentTimeMillis()}.$imgExt")

                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use { bos ->
                                its.copyToSuspend(bos)
                            }
                        }
                        imgFile = file
                    }
                    part.dispose()
                }
            }
        }
//        DbFactory.insertMediaReference()
    }
}