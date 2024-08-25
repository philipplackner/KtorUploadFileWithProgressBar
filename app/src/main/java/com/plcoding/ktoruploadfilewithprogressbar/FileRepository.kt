package com.plcoding.ktoruploadfilewithprogressbar

import android.net.Uri
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class FileRepository(
    private val httpClient: HttpClient,
    private val fileReader: FileReader,
) {

    fun uploadFile(contentUri: Uri): Flow<ProgressUpdate> = channelFlow {
        val info = fileReader.uriToFileInfo(contentUri)

        httpClient.submitFormWithBinaryData(
            url = "https://dlptest.com/https-post/",
            formData = formData {
                append("description", "Test")
                append("the_file", info.bytes, Headers.build {
                    append(HttpHeaders.ContentType, info.mimeType)
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=${info.name}"
                    )
                })
            }
        ) {
            onUpload { bytesSentTotal, totalBytes ->
                if(totalBytes > 0L) {
                    send(ProgressUpdate(bytesSentTotal, totalBytes))
                }
            }
        }
    }
}

data class ProgressUpdate(
    val bytesSent: Long,
    val totalBytes: Long
)