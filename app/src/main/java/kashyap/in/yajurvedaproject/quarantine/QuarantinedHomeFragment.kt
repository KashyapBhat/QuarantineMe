package kashyap.`in`.yajurvedaproject.quarantine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.utils.BiometricUtils
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.fragment_quarantined_home.*

class QuarantinedHomeFragment : BaseFragment(), HomeContract.view, BiometricUtils.BiometricIntf {

    private var dialog: BottomSheetDialog? = null
    private var homePresenter: HomeFragmentPresenter? = null
    private var biometricUtils: BiometricUtils? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            QuarantinedHomeFragment().apply {
                arguments = Bundle().apply {
                }
            }

        const val CAMERA_REQUEST = 1888
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        customShowAndHideProgress(true)
        return inflater.inflate(R.layout.fragment_quarantined_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricUtils = BiometricUtils(context, activity as BaseActivity?, this)
    }

    override fun afterFBDataFetch() {
        homePresenter = HomeFragmentPresenter(context, this, quarantine)
        customShowAndHideProgress(true)
        handleQuarantineOrNot()
        homePresenter?.showBanner(rvBanner)
        homePresenter?.handleStopWatch()
        btPhoto?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                biometricUtils?.biometrics()
            }
        }
        btIssue?.setOnClickListener { dialog = homePresenter?.showIssue() }
        btSubmit?.setOnClickListener { homePresenter?.onSubmitButtonClick(etTempValue) }
        btEmergency?.setOnClickListener { homePresenter?.handleEmergencyButton(getActivity()) }
        customShowAndHideProgress(false)
    }

    private fun customShowAndHideProgress(shouldShowProgress: Boolean) {
        if (shouldShowProgress) {
            (context as BaseActivity?)?.showProgress()
        } else Handler().postDelayed({ (context as BaseActivity?)?.hideProgress() }, 200)
    }

    private fun handleQuarantineOrNot() {
        cvMain?.visibility = if (PrefUtils.isQuarantined(context)) View.VISIBLE else View.GONE
        cvMainNonQuarantine?.visibility =
            if (!PrefUtils.isQuarantined(context)) View.VISIBLE else View.GONE
        btIssueNonQuarantine?.setOnClickListener { dialog = homePresenter?.showIssue() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras!!["data"] as Bitmap?
            photo?.let { homePresenter?.detectFace(it, this) }
        }
    }

    override fun handleImageButton(isEnabled: Boolean) {
        btPhoto?.text = if (isEnabled) "Add Image" else "Image Added"
        btPhoto?.setTextColor(resources.getColor(if (isEnabled) R.color.black else R.color.grey))
        btPhoto?.setBackgroundColor(resources.getColor(if (isEnabled) R.color.white else R.color.darker_grey))
        btPhoto?.isEnabled = isEnabled
    }

    override fun handleIssuesButton(isEnabled: Boolean) {
        btIssue?.text = if (isEnabled) "Add health problem" else "Added"
        btIssue?.setTextColor(resources.getColor(if (isEnabled) R.color.black else R.color.grey))
        btIssue?.setBackgroundColor(resources.getColor(if (isEnabled) R.color.white else R.color.darker_grey))
        btIssue?.isEnabled = isEnabled
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    override fun showHomeSnackBar(title: String, actionTitle: String, runnable: Runnable?) {
        showSnackBar(title, actionTitle, runnable)
    }

    override fun showToast(title: String, actionTitle: String) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onAuthenticationSuccess() {
        homePresenter?.takeImage(this)
    }

    override fun onAuthenticationFailed() {
        restartBiometrics()
    }

    override fun noHardwareFound() {
        homePresenter?.takeImage(this)
    }

    override fun biometricNotEnrolled() {
        (activity as BaseActivity).openSettings()
    }

    private fun restartBiometrics() {
        activity?.finish()
        startActivity(activity?.intent)
        activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
    }
}