package com.example.andreirybin.janetest.activities.util

import android.content.Context
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadManager {
    companion object {
        fun writeResponseBodyToDisk(body: ResponseBody?, imageId: String?, context: Context?): String? {
            try {
                val file = File("${context?.getExternalFilesDir(null)}${File.separator}$imageId")
                var fileSizeDownloader = 0
                body?.let {
                    val inputStream = it.byteStream()
                    val outputStream = FileOutputStream(file)

                    try {
                        val fileReader = ByteArray(4096)
                        while (true) {
                            val read = inputStream.read(fileReader)
                            if(read == -1) break

                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloader += read
                        }
                        outputStream.flush()
                    } catch (innerError: IOException) {
                        return null
                    } finally {
                        inputStream.close()
                        outputStream.close()
                    }
                    return file.absolutePath
                } ?: return null

            } catch (outerError: IOException) {
                return null
            }
        }
    }
}