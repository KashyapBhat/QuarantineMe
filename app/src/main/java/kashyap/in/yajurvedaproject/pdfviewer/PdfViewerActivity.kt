package kashyap.`in`.yajurvedaproject.pdfviewer

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.common.*
import kashyap.`in`.yajurvedaproject.custom.CustomPdfAdapter
import kashyap.`in`.yajurvedaproject.custom.CustomPdfViewer
import kashyap.`in`.yajurvedaproject.receivers.NetworkReceiver
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.changeButtonPosition
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.shareApp
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.slideDown
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.slideUp
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kashyap.`in`.yajurvedaproject.webview.CustomWebViewClient
import kashyap.`in`.yajurvedaproject.worker.AlarmWorker
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import java.io.File
import java.util.*

class PdfViewerActivity : BaseActivity(), CustomWebViewClient.WebViewClientIntf,
    BottomNavigationView.OnNavigationItemSelectedListener, PdfViewerContract.PdfView {

    private lateinit var pdfViewerPresenter: PdfViewerPresenterImpl

    private lateinit var pdfViewPager: CustomPdfViewer
    private lateinit var pdfViewAdapter: CustomPdfAdapter
    private var pdfViewPagerPosition: Int = 0

    private var brightness: Int = 0
    private var navBarShouldGoUp: Boolean = false
    private var isShowingSeekbar: Boolean = false
    private var loadComplete: Boolean = false
    private var file: File? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContentView(R.layout.activity_pdf_viewer)
        pdfViewerPresenter = PdfViewerPresenterImpl(this, this)
        pdfViewerPresenter.setUpWebview(wvPdfRenderer, this)
        setBottomNavForFirstTime()
        changeSeekBarVisibility(View.GONE)
        checkPermissionsAndRun()
    }

    private fun setBottomNavForFirstTime() {
        bottomNav.setOnNavigationItemSelectedListener(this)
        showOrHideOptions()
        buttonOptions.setOnClickListener {
            showOrHideOptions()
        }
    }

    private fun startProcesses() {
        if (pdfViewerPresenter.containsFile())
            file = File(
                context.filesDir,
                pdfViewerPresenter.getFilePath()
            )
        if (NetworkReceiver.isNetworkAvailable(this)) {
            if (!loadComplete)
                pdfViewerPresenter.getPath()
        } else {
            if (file != null && file?.exists() == true) {
                openViewToShowPdf(file, "")
            } else {
                showSnackBar(
                    getString(R.string.internet_not_connected),
                    getString(
                        R.string.retry
                    ),
                    Runnable { startProcesses() })
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        (pdfViewPager.adapter as CustomPdfAdapter)?.close()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ONE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleBrightness()
                    checkSeekbarIsVisibleAndShow()
                } else {
                    changeSeekBarVisibility(View.GONE)
                    showSnackBar(
                        getString(R.string.not_below_m),
                        getString(R.string.sorry),
                        null
                    )
                }
                return true
            }
            MENU_TWO -> {
                changeOrientation()
                showOrHideOptions()
                return true
            }
            MENU_THREE -> {
                pdfViewerPresenter.checkForReminderAndAdd()
                return true
            }
            MENU_FOUR -> {
                shareApp(this)
                return true
            }
        }
        return false
    }

    override fun setRemainder() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            onTimeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.setTitle("Set Alarm Time")
        timePickerDialog.show()
    }

    private var onTimeSetListener: OnTimeSetListener =
        OnTimeSetListener { _, hourOfDay, minute ->
            AlarmWorker.startLogoutWorker(
                context, hourOfDay, minute, REMINDER_WORKER
            )
            PrefUtils.saveToPrefs(context, IS_REMINDER_ALREADY_SET, true)
            showSnackBar(
                "Alarm set for $hourOfDay : $minute",
                "Okay",
                null
            )
        }

    private fun checkSeekbarIsVisibleAndShow() {
        if (pdfViewerPresenter.checkChangeSettingsGranted() && !isShowingSeekbar) {
            isShowingSeekbar = true
            changeSeekBarVisibility(View.VISIBLE)
        } else {
            isShowingSeekbar = false
            changeSeekBarVisibility(View.GONE)
        }
    }

    override fun openViewToShowPdf(file: File?, url: String) {
        hideProgress()
        if (file == null || loadComplete)
            return
        showProgress()
        setUpPagerAdapter(url)
    }

    private fun setUpPagerAdapter(url: String) {
        try {
            file = File(
                context.filesDir,
                pdfViewerPresenter.getFilePath()
            )
            if (file?.exists() == true) {
                pdfViewPager = CustomPdfViewer(this, file?.absolutePath)
                pdfViewAdapter = pdfViewPager.adapter as CustomPdfAdapter
                llPdfRoot.addView(pdfViewPager)
                (pdfViewPager as ViewPager).currentItem = 0
                (pdfViewPager as ViewPager).setOnPageChangeListener(object :
                    ViewPager.OnPageChangeListener {

                    override fun onPageSelected(position: Int) {
                        pdfViewPagerPosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(
                        position: Int, positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }
                })
            } else {
                loadWV(url.trim())
            }
        } catch (e: Exception) {
            loadWV(url.trim())
        }
        loadComplete = true
    }

    private fun loadWV(url: String) {
        if (url.isEmpty())
            return
        val endUrl: String = pdfViewerPresenter.getFormattedUrlToRender(url)
        llPdfRoot?.visibility = View.GONE
        wvPdfRenderer?.visibility = View.VISIBLE
        wvPdfRenderer?.loadUrl(endUrl.trim())
    }

    private fun handleBrightness() {
        brightness =
            Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
        seekBar.progress = brightness
        pdfViewerPresenter.checkChangeSettingsGranted()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (pdfViewerPresenter.checkChangeSettingsGranted())
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS, progress
                    )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun changeSeekBarVisibility(visibility: Int) {
        rlSeekBar.visibility = visibility
        seekBar.visibility = visibility
    }

    private fun showOrHideOptions() {
        if (navBarShouldGoUp) {
            bottomNav.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
            slideUp(bottomNav)
            changeButtonPosition(this, buttonOptions, 70)
        } else {
            slideDown(bottomNav)
            bottomNav.visibility = View.GONE
            bottomBar.visibility = View.GONE
            if (isShowingSeekbar) {
                changeSeekBarVisibility(View.GONE)
                isShowingSeekbar = false
            }
            changeButtonPosition(this, buttonOptions, 15)
        }
        navBarShouldGoUp = !navBarShouldGoUp
    }

    override fun networkChanged() {
        checkPermissionsAndRun()
    }

    override fun onAllPermissionsAcquired() {
        startProcesses()
    }

    override fun loadingStarted() {
    }

    override fun loadingEnded() {
        hideProgress()
    }

    override fun showToast(title: String, actionText: String, runnable: Runnable?) {
        showSnackBar(title, actionText, runnable)
    }

    override fun showLoading() {
        showProgress()
    }

    override fun hideLoading() {
        hideProgress()
    }
}

