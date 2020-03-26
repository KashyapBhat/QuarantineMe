package kashyap.`in`.yajurvedaproject.base

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kashyap.`in`.yajurvedaproject.BuildConfig
import kashyap.`in`.yajurvedaproject.common.*
import kashyap.`in`.yajurvedaproject.receivers.NetworkReceiver
import kashyap.`in`.yajurvedaproject.custom.CustomSnackbar
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.updateUppFromPlaystore
import kashyap.`in`.yajurvedaproject.utils.PermissionsHandler.checkAndRequestPermissions
import kashyap.`in`.yajurvedaproject.utils.PermissionsHandler.isIsPermissionsChecksRunning
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.activity_base.*
import java.io.File


abstract class BaseActivity : AppCompatActivity(), NetworkReceiver.NetworkChangeListener {

    private lateinit var baseLayout: RelativeLayout
    private lateinit var networkReceiver: NetworkReceiver
    protected lateinit var context: Context
    private var customSnackbar: CustomSnackbar? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        baseLayout = LayoutInflater.from(this)
            .inflate(kashyap.`in`.yajurvedaproject.R.layout.activity_base, null) as RelativeLayout
        setContentView(baseLayout)
        setStub(layoutResID)
        initView()
    }

    private fun setStub(layoutResID: Int) {
        val viewStub =
            baseLayout.findViewById<ViewStub>(kashyap.`in`.yajurvedaproject.R.id.container)
        viewStub.layoutResource = layoutResID
        viewStub.inflate()
        context = this
    }

    fun initView() {
        hideProgress()
    }

    fun showProgress() {
        rlLoader.visibility = View.VISIBLE
        baseProgress.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun hideProgress() {
        rlLoader.visibility = View.GONE
        baseProgress.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onResume() {
        super.onResume()
        setNetworkReceiver()
        if (needsUpdate())
            updateUppFromPlaystore(this)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    private fun setNetworkReceiver() {
        networkReceiver = NetworkReceiver()
        networkReceiver.setNetworkChangeListener(this)
        val intentFilter = IntentFilter(INTENT_CONNECTIVITY_CHANGE)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onNetworkChanged() {
        networkChanged()
    }

    abstract fun networkChanged()

    private fun needsUpdate(): Boolean {
        if (BuildConfig.VERSION_CODE < PrefUtils.getFromPrefs(
                context,
                FS_MIN_APP_VERSION_KEY,
                DEFAULT_MIN_APP_VERSION
            ) as Int
        ) {
            return true
        }
        return false
    }

    fun openUsingNativePdfRenderer(
        activity: Activity,
        file: File,
        fileName: String,
        iV: ImageView
    ) {
        val application = activity.application
        if (!file.exists()) {
            application.assets.open(fileName).use { asset ->
                file.writeBytes(asset.readBytes())
            }
        }
        var currentPage = 1
        val pdfRenderer: PdfRenderer
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).also {
            pdfRenderer = PdfRenderer(it)
        }
        pdfRenderer
            .let { renderer: PdfRenderer ->
                val page: PdfRenderer.Page = renderer.openPage(currentPage).also {
                    currentPage = it.index
                }
                val bitmap = Bitmap.createBitmap(
                    activity.resources.displayMetrics.densityDpi * page.width,
                    activity.resources.displayMetrics.densityDpi * page.height,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                iV.setImageBitmap(bitmap)
            }
    }

    fun showSnackBar(title: String, actionText: String, runnable: Runnable?) {
        if (customSnackbar == null) {
            customSnackbar = CustomSnackbar.make(
                window.decorView.findViewById(android.R.id.content),
                CustomSnackbar.LENGTH_INDEFINITE
            )
        }
        customSnackbar?.setText(title)
        customSnackbar?.setAction(actionText) {
            runnable?.run()
        }
        if (customSnackbar?.isShownOrQueued == false) {
            customSnackbar?.show()
        }
    }

    fun checkPermissionsAndRun() {
        if (isIsPermissionsChecksRunning)
            return
        if (checkAndRequestPermissions(
                this,
                GeneralUtils.getPermissionRequired().toTypedArray()
            )
        ) {
            onAllPermissionsAcquired()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissionsAndRun()
    }

    abstract fun onAllPermissionsAcquired()

    fun changeOrientation() {
        requestedOrientation =
            if (checkOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
    }

    private fun showToolbar() {
        toolbar.visibility = View.VISIBLE
        toolbarText.visibility = View.VISIBLE
    }

    private fun hideToolbar() {
        toolbar.visibility = View.GONE
        toolbarText.visibility = View.GONE
    }

    fun checkOrientation(): Int {
        return resources.configuration.orientation
    }

}
