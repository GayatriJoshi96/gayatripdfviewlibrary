package com.gayatri.pdfview

import PdfGjLibrary
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var pdfFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pdfViewer = PdfGjLibrary(this)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val buttonOpenPdf = findViewById<Button>(R.id.buttonOpenPdf)
        val buttonDownloadPdf = findViewById<Button>(R.id.buttonDownloadPdf)

        buttonOpenPdf.setOnClickListener {
            val inputStream = resources.openRawResource(R.raw.sample)
            pdfFile = File(cacheDir, "sample.pdf")

            inputStream.use { input ->
                pdfFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (pdfFile.exists()) {
                Log.d("MainActivity", "PDF copied successfully to: ${pdfFile.absolutePath}")
                pdfViewer.viewPdfs(pdfFile, imageView, 0) // Render the first page
            } else {
                Log.e("MainActivity", "Failed to copy PDF to cache")
            }
        }

        buttonDownloadPdf.setOnClickListener {
            if (::pdfFile.isInitialized && pdfFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    downloadPdfUsingMediaStore(pdfFile)
                } else {
                    downloadPdfLegacy(pdfFile)
                }
            } else {
                Toast.makeText(this, "PDF not found!", Toast.LENGTH_SHORT).show()
            }
        }
        }



    // For Android 10+ (API 29+) use MediaStore to save PDF to Downloads folder
    private fun downloadPdfUsingMediaStore(file: File) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "sample.pdf") // File name
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf") // MIME type
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/") // Save directly in Downloads folder
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            if (uri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)

                if (outputStream != null) {
                    file.inputStream().use { input ->
                        input.copyTo(outputStream)
                    }
                    Toast.makeText(this, "PDF downloaded to Downloads folder", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to download PDF!", Toast.LENGTH_SHORT).show()
        }
    }

    // For Android versions below API 29
    private fun downloadPdfLegacy(file: File) {
        try {
            val downloadsFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.pdf")

            if (!downloadsFolder.exists()) {
                downloadsFolder.parentFile?.mkdirs()
            }

            file.copyTo(downloadsFolder, overwrite = true)
            Toast.makeText(this, "PDF downloaded to Downloads folder", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to download PDF!", Toast.LENGTH_SHORT).show()
        }
    }
}
