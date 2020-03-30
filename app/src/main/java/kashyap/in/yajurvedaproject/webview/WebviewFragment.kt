package kashyap.`in`.yajurvedaproject.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.WEBVIEW_URL
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import kotlinx.android.synthetic.main.fragment_webview.*

class WebviewFragment : BaseFragment(), CustomWebViewClient.WebViewClientIntf {
    private var url: String? = ""

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            WebviewFragment().apply {
                arguments = Bundle().apply {
                    putString(WEBVIEW_URL, url)
                }
            }
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(WEBVIEW_URL).orEmpty()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgress()
        setUpWebview(webView, this)
    }

    override fun afterFBDataFetch() {
    }

    override fun showProgress() {
        srWebview?.isRefreshing = true
    }

    override fun hideProgress() {
        srWebview?.isRefreshing = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setUpWebview(webview: WebView?, client: CustomWebViewClient.WebViewClientIntf) {
        val settings: WebSettings? = webview?.settings
        settings?.javaScriptEnabled = true
        settings?.allowFileAccessFromFileURLs = true
        settings?.allowUniversalAccessFromFileURLs = true
        settings?.builtInZoomControls = true
        settings?.builtInZoomControls = false
        settings?.pluginState = WebSettings.PluginState.ON
        webview?.webChromeClient = WebChromeClient()
        webview?.webViewClient = CustomWebViewClient(client)
        webview?.setOnLongClickListener { true }
        webview?.isLongClickable = false
        webview?.isHapticFeedbackEnabled = false
        webview?.visibility = View.VISIBLE
        webview?.loadUrl(url?.trim())
    }

    override fun loadingStarted() {
        showProgress()
    }

    override fun loadingEnded() {
        hideProgress()
    }

}
