package com.skichrome

import com.skichrome.model.DbFactory
import com.skichrome.model.JsonListResponseOk
import com.skichrome.model.JsonResponseOk
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
    route("/upload") {
        upload()
    }

    get("/agent-id={agent}") {
        call.parameters["agent"]?.toLongOrNull()?.let {
            val response = DbFactory.getAllMediaReferenceFromAgent(it)
            call.respond(JsonListResponseOk(result = response))
        }
    }
    get("/{mediaRefId}") {
        // todo return url of picture to display on client
        val ref = call.parameters["mediaRefId"]
        ref?.let {
            call.respond(mapOf("OK" to true, "url" to it))
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "OK" to false)
    }
    post("/delete-delta") {
        val mediaRefData = call.receive<Array<MediaReferenceData>>().toList()
        val response = DbFactory.deleteUnavailableMediaRef(mediaRefData)
        call.respond(JsonResponseOk(result = response))
    }
}

fun Route.upload()
{
    post("/") {
        val multipart = call.receiveMultipart()
        var description = ""
        var agentId = -1L
        var realtyId = -1L
        var imgId = -1L
        val workDirFile = File(System.getProperty("user.dir") + "/media_references")
        var outputFileName = "upload-${System.currentTimeMillis()}"
        var imgFile: File? = null
        multipart.forEachPart { part ->
            when (part)
            {
                is PartData.FormItem ->
                {
                    if (part.name == "title")
                        description = part.value
                    if (part.name == "agent_id")
                        agentId = part.value.toLongOrNull() ?: -1L
                    if (part.name == "realty_id")
                        realtyId = part.value.toLongOrNull() ?: -1L
                    if (part.name == "id")
                        imgId = part.value.toLongOrNull() ?: -1L
                }
                is PartData.FileItem ->
                {
                    part.originalFileName?.let {
                        val imgExt = File(it).extension
                        outputFileName += ".$imgExt"

                        workDirFile.mkdirs()

                        val file = File(workDirFile, outputFileName)

                        println("File folder : $workDirFile")
                        println("Server file location : $outputFileName")

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

        println("Img file field : $imgFile")

        if (agentId != -1L || description != "" || realtyId != -1L || imgId != -1L || imgFile != null)
        {
            val mediaRefUrl = "https://${call.request.headers["Host"]}/real-estate/media-references/$outputFileName"
            val insertedMediaRefId = DbFactory.insertMediaReference(MediaReferenceData(
                    reference = mediaRefUrl,
                    agent_id = agentId,
                    short_desc = description,
                    realty_id = realtyId,
                    id = imgId),
                    imgFile!!.absolutePath
            )
            call.respond(mapOf("OK" to true, "id" to insertedMediaRefId))
        } else
            call.respond(HttpStatusCode.InternalServerError, mapOf("OK" to false))
    }
}