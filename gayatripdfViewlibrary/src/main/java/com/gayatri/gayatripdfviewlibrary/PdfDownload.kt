import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfDownload {
    fun pdfDownload(url: String, destination: File, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { inputStream ->
                        FileOutputStream(destination).use { outputStream ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        callback(true)
                    }
                } else {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        callback(false)
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    callback(false)
                }
            }
        }.start()
    }
}
