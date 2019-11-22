package com.skichrome

import com.skichrome.model.DbFactory
import com.skichrome.model.JsonListResponseOk
import com.skichrome.model.MediaReferenceData
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
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
            val response = DbFactory.getAllMediaReferenceFromAgent(it)
            call.respond(JsonListResponseOk(result = response))
        }
    }
    post("/") {
        val mediaRefData = call.receive<Array<MediaReferenceData>>().toList()
        DbFactory.insertMediaReferenceList(mediaRefData)
        call.respond(mapOf("OK" to true))
    }
    get("/{mediaRefId}") {
        val ref = call.parameters["mediaRefId"]
        ref?.let {
            call.respond(mapOf("OK" to true, "url" to it))
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "OK" to false)
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
        var outputFileName = "upload-${System.currentTimeMillis()}"
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
                        outputFileName += ".$imgExt"
                        val file = File("/", outputFileName)

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
        val mediaRefUrl = "http://192.168.0.24:8080/real-estate/media-references/$outputFileName"
        val insertedMediaRefId = DbFactory.insertMediaReference(MediaReferenceData(reference = mediaRefUrl, agent_id = 1L, short_desc = "", realty_id = 1))
        call.respond(mapOf("OK" to true, "id" to insertedMediaRefId))
    }
}