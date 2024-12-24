import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

class PdfRenderGJ(private val context: Context) {
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null

    fun openPdf(file: File) {
        if (!file.exists()) {
            throw IllegalArgumentException("PDF file does not exist: ${file.absolutePath}")
        }
        try {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Failed to open PDF file: ${file.absolutePath}")
        }
    }

    fun renderPage(pageIndex: Int, bitmap: Bitmap) {
        if (pdfRenderer == null) {
            throw IllegalStateException("PDF file is not opened. Call openPdf() first.")
        }

        if (pageIndex < 0 || pageIndex >= getPageCount()) {
            throw IndexOutOfBoundsException("Invalid page index: $pageIndex")
        }

        try {
            currentPage?.close() // Close the previously opened page
            currentPage = pdfRenderer?.openPage(pageIndex)
            currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("Failed to render page $pageIndex")
        }
    }

    fun getPageCount(): Int {
        return pdfRenderer?.pageCount ?: 0
    }

    fun close() {
        try {
            currentPage?.close()
            pdfRenderer?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
