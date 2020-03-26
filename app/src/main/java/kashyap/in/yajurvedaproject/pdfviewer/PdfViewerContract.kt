package kashyap.`in`.yajurvedaproject.pdfviewer

import android.webkit.WebView
import kashyap.`in`.yajurvedaproject.base.BaseView
import kashyap.`in`.yajurvedaproject.webview.CustomWebViewClient
import java.io.File

/**
 * Created by Kashyap Bhat on 2019-12-18.
 */
interface PdfViewerContract {
    interface PdfView : BaseView {
        fun openViewToShowPdf(file: File?, url: String)
        fun setRemainder()
    }

    interface PdfPresenterInt {
        fun getPath()
        fun checkForReminderAndAdd()
        fun getFormattedUrlToRender(url: String): String
        fun checkChangeSettingsGranted(): Boolean
        fun setUpWebview(
            wvPdfRenderer: WebView?,
            client: CustomWebViewClient.WebViewClientIntf
        )

        fun containsFile(): Boolean
        fun removeRemainder()
        fun getFilePath(): String
    }
}