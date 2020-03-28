package kashyap.`in`.yajurvedaproject.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.location.Location
import android.os.ParcelFileDescriptor
import android.preference.PreferenceManager
import android.preference.PreferenceManager.OnActivityResultListener
import android.view.LayoutInflater
import android.view.ViewStub
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kashyap.`in`.yajurvedaproject.BuildConfig
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.common.DEFAULT_MIN_APP_VERSION
import kashyap.`in`.yajurvedaproject.common.FS_MIN_APP_VERSION_KEY
import kashyap.`in`.yajurvedaproject.common.INTENT_CONNECTIVITY_CHANGE
import kashyap.`in`.yajurvedaproject.receivers.NetworkReceiver
import kashyap.`in`.yajurvedaproject.utils.*
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.updateUppFromPlaystore
import kashyap.`in`.yajurvedaproject.utils.PermissionsHandler.checkAndRequestPermissions
import kashyap.`in`.yajurvedaproject.utils.PermissionsHandler.isIsPermissionsChecksRunning
import kotlinx.android.synthetic.main.activity_base.*
import java.io.File
import android.view.View as View1


abstract class BaseActivity : AppCompatActivity(), NetworkReceiver.NetworkChangeListener,
    LocationCallbacks {

    private lateinit var baseLayout: RelativeLayout
    private lateinit var networkReceiver: NetworkReceiver
    protected lateinit var context: Context
    protected var locationFetcher: LocationUtils? = null
    private var onActivityResultListener: OnActivityResultListener? = null

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
        locationFetcher = LocationUtils(this, this)
    }

    override fun showProgress() {
        rlLoader.visibility = View1.VISIBLE
        baseProgress.visibility = View1.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun hideProgress() {
        rlLoader.visibility = View1.GONE
        baseProgress.visibility = View1.GONE
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
        GeneralUtils.showSnackBar(title, window, actionText, runnable)
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
        toolbar.visibility = View1.VISIBLE
        toolbarText.visibility = View1.VISIBLE
    }

    private fun hideToolbar() {
        toolbar.visibility = View1.GONE
        toolbarText.visibility = View1.GONE
    }

    fun checkOrientation(): Int {
        return resources.configuration.orientation
    }

    fun openSettings() {
        startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0)
    }

    fun addFragment(activity: BaseActivity, baseFragment: BaseFragment?, @IdRes containerId: Int) {
        GeneralUtils.transact(activity, baseFragment, false, containerId)
    }

    fun replaceFragment(
        activity: BaseActivity,
        baseFragment: BaseFragment?, @IdRes containerId: Int
    ) {
        GeneralUtils.transact(activity, baseFragment, true, containerId)
    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationFetcher?.initiateCurrentLocation()
        }
    }

    abstract override fun onLocationResult(location: Location?)
    override fun onGpsUnavailable() {
    }

    override fun registerActivityResult(onActivityResultListener: PreferenceManager.OnActivityResultListener) {
        this.onActivityResultListener = onActivityResultListener;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CHECK_SETTINGS -> onActivityResultListener?.onActivityResult(
                    requestCode,
                    resultCode,
                    data
                )
            }
        }
    }
}