package cn.jinelei.rainbow.app.handler

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*

class CustomCrashHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        val stackTraceInfo = getStackTraceInfo(e)
        GlobalScope.launch(Dispatchers.IO) {
            saveToFile(stackTraceInfo)
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    private fun getStackTraceInfo(e: Throwable?): String? {
        val writer = StringWriter()
        val printWriter: PrintWriter? = PrintWriter(writer)
        return try {
            e?.printStackTrace(printWriter)
            writer.toString()
        } catch (exception: Exception) {
            null
        } finally {
            printWriter?.close()
        }
    }

    private fun saveToFile(message: String?) {
        if (message == null)
            return
        val file = File(logFilePath)
        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (!mkdirs)
                return
        }
        val outputStream = FileOutputStream(File(file, "${System.currentTimeMillis()}.log"))
        try {
            val inputStream = ByteArrayInputStream(message.toByteArray())
            inputStream.copyTo(outputStream)
            Log.e(TAG, "save crash log success")
        } catch (e: Exception) {
            Log.e(TAG, "save crash log taken exception ${e.message}")
        } finally {
            outputStream.close()
        }
    }

    companion object {
        val TAG = CustomCrashHandler::class.java.simpleName ?: "CustomCrashHandler"
        val logFilePath =
            "${Environment.getExternalStorageDirectory()}${File.separator}Android${File.separator}data${File.separator}cn.jinelei.rainbow${File.separator}crashLog"
        val instance = CustomCrashHandler()
    }
}