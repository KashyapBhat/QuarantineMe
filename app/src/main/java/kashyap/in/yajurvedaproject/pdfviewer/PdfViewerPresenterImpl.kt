package kashyap.`in`.yajurvedaproject.pdfviewer

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.common.*
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kashyap.`in`.yajurvedaproject.webview.CustomWebViewClient
import kashyap.`in`.yajurvedaproject.worker.AlarmWorker
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


/**
 * Created by Kashyap Bhat on 2019-12-18.
 */
class PdfViewerPresenterImpl(private val pdfView: PdfViewerContract.PdfView, val context: Context) :
    PdfViewerContract.PdfPresenterInt {

    @SuppressLint("SetJavaScriptEnabled")
    override fun setUpWebview(
        wvPdfRenderer: WebView?,
        client: CustomWebViewClient.WebViewClientIntf
    ) {
        val settings: WebSettings? = wvPdfRenderer?.settings
        settings?.javaScriptEnabled = true
        settings?.allowFileAccessFromFileURLs = true
        settings?.allowUniversalAccessFromFileURLs = true
        settings?.builtInZoomControls = true
        settings?.builtInZoomControls = false
        settings?.pluginState = WebSettings.PluginState.ON
        wvPdfRenderer?.webChromeClient = WebChromeClient()
        wvPdfRenderer?.webViewClient = CustomWebViewClient(client)
        wvPdfRenderer?.setOnLongClickListener { true }
        wvPdfRenderer?.isLongClickable = false
        wvPdfRenderer?.isHapticFeedbackEnabled = false
    }

    override fun getPath() {
        pdfView.showLoading()
        val db = FirebaseFirestore.getInstance()
        db.collection(FS_COLLECTION_NAME)
            .document(FS_DOCUMENT_NAME)
            .get()
            .addOnSuccessListener {
                Log.d("Firebase Document:", "Data: " + it.data?.entries)
                downloadFile(
                    it?.data?.get(FS_FB_STORAGE_PATH_KEY) as String,
                    it?.data?.get(FS_PHONE_STORAGE_PATH_KEY) as String,
                    it?.data?.get(FS_PHONE_STORAGE_FILE_NAME_KEY) as String,
                    it?.data?.get(FS_SHOULD_REFRESH_KEY) as Boolean
                )
                it.data?.let { it1 -> saveInSharedPrefs(it1) }
            }.addOnFailureListener { exception ->
                onError(exception)
            }
    }

    private fun saveInSharedPrefs(data: MutableMap<String, Any>?) {
        PrefUtils.saveToPrefs(context, FS_FB_STORAGE_PATH_KEY, data?.get(FS_FB_STORAGE_PATH_KEY))
        PrefUtils.saveToPrefs(
            context, FS_PHONE_STORAGE_FILE_NAME_KEY,
            data?.get(FS_PHONE_STORAGE_FILE_NAME_KEY)
        )
        PrefUtils.saveToPrefs(
            context, FS_PHONE_STORAGE_PATH_KEY,
            data?.get(FS_PHONE_STORAGE_PATH_KEY)
        )
        PrefUtils.saveToPrefs(context, FS_SHOULD_REFRESH_KEY, data?.get(FS_SHOULD_REFRESH_KEY))
        PrefUtils.saveToPrefs(
            context, FS_GOOGLE_DOC_RENDERER_URL_KEY,
            data?.get(FS_GOOGLE_DOC_RENDERER_URL_KEY)
        )
        PrefUtils.saveToPrefs(context, FS_PDF_STORAGE_URL_KEY, data?.get(FS_PDF_STORAGE_URL_KEY))
        PrefUtils.saveToPrefs(context, FS_MIN_APP_VERSION_KEY, data?.get(FS_MIN_APP_VERSION_KEY))
    }

    private fun downloadFile(
        childPath: String,
        folderName: String,
        fileName: String,
        shouldRefresh: Boolean
    ) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val pdfRef: StorageReference = storage.reference.child(childPath)
        val direct = File(context.filesDir, folderName)
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val file = File(context.filesDir, "$folderName/$fileName")
        if (!file.exists() || shouldRefresh) {
            getDownloadUrlFromFireBase(pdfRef, file)
        } else {
            pdfView.openViewToShowPdf(file, "")
        }
    }

    private fun getDownloadUrlFromFireBase(pdfRef: StorageReference, file: File) {
        pdfRef
            .downloadUrl
            .addOnSuccessListener {
                Log.d("Firebase: ", "Url created $it")
                if (!file.exists()) {
                    saveToFileFromFirebase(pdfRef, file, it.toString())
                } else {
                    pdfView.openViewToShowPdf(file, it.toString())
                }
            }.addOnFailureListener { exception ->
                onError(exception)
            }
    }

    private fun saveToFileFromFirebase(pdfRef: StorageReference, file: File, url: String) {
        pdfRef
            .getFile(file)
            .addOnSuccessListener {
                Log.d("Firebase: ", "Local file created $it")
                pdfView.openViewToShowPdf(file, url)
            }.addOnFailureListener { exception ->
                onError(exception)
            }
    }

    private fun onError(exception: Exception) {
        Log.e(" Firebase Error: ", "" + exception)
        pdfView.hideLoading()
        pdfView.showToast(
            context.getString(R.string.error),
            context.getString(R.string.reload),
            Runnable { getPath() }
        )
    }

    override fun getFormattedUrlToRender(url: String): String {
        var endUrl = ""
        try {
            endUrl = PrefUtils.getFromPrefs(
                context,
                FS_GOOGLE_DOC_RENDERER_URL_KEY,
                DEFAULT_DOCS_RENDERER_URL
            ) as String + URLEncoder.encode(url, context.getString(R.string.utf_8))
            Log.d("End url: ", "" + endUrl)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return endUrl
    }

    override fun checkChangeSettingsGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                return true
            } else {
                pdfView.showToast(
                    context.getString(R.string.grant_permission_to_modify_settings),
                    context.getString(R.string.go_to_settings),
                    Runnable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            intent.data = Uri.parse("package:" + context.packageName)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                            }
                        }
                    }
                )
                return false
            }
        }
        return false
    }

    override fun checkForReminderAndAdd() {
        if (PrefUtils.hasKey(context, IS_REMINDER_ALREADY_SET) && PrefUtils.getFromPrefs(
                context,
                IS_REMINDER_ALREADY_SET,
                false
            ) as Boolean
        ) {
            GeneralUtils.showDialogWithButtons(
                context, context.getString(R.string.remainder),
                context.getString(R.string.remainder_already_set),
                context.getString(R.string.remove), context.getString(R.string.update),
                Runnable {
                    removeRemainder()
                },
                Runnable {
                    pdfView.setRemainder()
                }
                , true
            )
        } else {
            pdfView.setRemainder()
        }
    }

    override fun removeRemainder() {
        AlarmWorker.stopLogoutWorker(context, REMINDER_WORKER)
        PrefUtils.saveToPrefs(context, IS_REMINDER_ALREADY_SET, false)
    }


    override fun containsFile(): Boolean {
        return PrefUtils.hasKey(context, FS_PHONE_STORAGE_PATH_KEY)
                && PrefUtils.hasKey(context, FS_PHONE_STORAGE_FILE_NAME_KEY)
    }

    override fun getFilePath(): String {
        return PrefUtils.getFromPrefs(
            context, FS_PHONE_STORAGE_PATH_KEY,
            DEFAULT_FOLDER_NAME
        ) as String + "/" + PrefUtils.getFromPrefs(
            context, FS_PHONE_STORAGE_FILE_NAME_KEY,
            DEFAULT_FILE_NAME
        ) as String
    }

}