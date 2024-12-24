import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import java.io.File

class PdfGjLibrary(private val context: Context) {
    private val pdfRendererHelper = PdfRenderGJ(context)
    private val pdfDownloader = PdfDownload()

    // View a PDF
    fun viewPdf(file: File, bitmap: Bitmap, pageIndex: Int) {
        try {
            if (!file.exists()) {
                Log.e("PdfGjLibrary", "PDF file does not exist: ${file.absolutePath}")
                return
            }
            pdfRendererHelper.openPdf(file)
            pdfRendererHelper.renderPage(pageIndex, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PdfGjLibrary", "Error viewing PDF: ${e.message}")
        } finally {
            pdfRendererHelper.close()
        }
    }

    // View a PDF directly on an ImageView
    fun viewPdfs(file: File, imageView: ImageView, pageIndex: Int) {
        val width = if (imageView.width > 0) imageView.width else context.resources.displayMetrics.widthPixels
        val height = if (imageView.height > 0) imageView.height else context.resources.displayMetrics.heightPixels
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        viewPdf(file, bitmap, pageIndex)
        imageView.setImageBitmap(bitmap)
    }

    // Download and view a PDF
    fun downloadAndViewPdf(url: String, imageView: ImageView, pageIndex: Int, callback: (Boolean) -> Unit) {
        downloadPdf(url) { file ->
            if (file != null) {
                viewPdfs(file, imageView, pageIndex)
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    // Helper method to download a PDF
    private fun downloadPdf(url: String, onComplete: (File?) -> Unit) {
        val file = File(context.cacheDir, "downloaded_pdf.pdf")
        pdfDownloader.pdfDownload(url, file) { success ->
            if (success && file.exists()) {
                Log.d("PdfGjLibrary", "PDF downloaded successfully: ${file.absolutePath}")
                onComplete(file)
            } else {
                Log.e("PdfGjLibrary", "PDF download failed.")
                onComplete(null)
            }
        }
    }
}
