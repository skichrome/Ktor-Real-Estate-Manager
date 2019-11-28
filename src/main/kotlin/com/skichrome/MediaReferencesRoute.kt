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
import io.ktor.response.respondFile
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
    // Test command with curl :
    // curl -v -F id=1 -F title=test -F agent_id=1 -F realty_id=1 -F upload=@detective_main_app_logo.png http://localhost:8080/real-estate/media-references/upload
    get("/{mediaRefId}") {
        val ref = call.parameters["mediaRefId"]
        ref?.let {
            val requestedFile = File(System.getProperty("user.dir") + "/media_references", it)
            if (requestedFile.exists())
                call.respondFile(requestedFile)
            else
                call.respond(HttpStatusCode.NotFound)
        }
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