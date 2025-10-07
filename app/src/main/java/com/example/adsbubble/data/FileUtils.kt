package com.example.adsbubble.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    fun getFileFromUri(ctx: Context, uri: Uri): File {
        val input: InputStream? = ctx.contentResolver.openInputStream(uri)
        val temp = File.createTempFile("upload_", ".tmp", ctx.cacheDir)
        val out = FileOutputStream(temp)
        input?.copyTo(out)
        out.flush(); out.close(); input?.close()
        return temp
    }
}
