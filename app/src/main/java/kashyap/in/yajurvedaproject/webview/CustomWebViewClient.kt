package kashyap.`in`.yajurvedaproject.webview

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient(private val webViewClientInf: WebViewClientIntf) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView, url: String
    ): Boolean {
        view.loadUrl(url)
        webViewClientInf.loadingEnded()
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        webViewClientInf.loadingStarted()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.loadUrl(
            "javascript:(function() { " +
                    "document.querySelector('[role=\"toolbar\"]').remove();})()"
        )
        view?.loadUrl(
            "javascript:(function() { " +
                    "document.querySelector('.ndfHFb-aZ2wEe').remove();})()"
        )
        webViewClientInf.loadingEnded()
    }

    interface WebViewClientIntf {
        fun loadingStarted()
        fun loadingEnded()
    }
}
