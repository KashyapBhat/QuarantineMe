package kashyap.`in`.yajurvedaproject.splash

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.login.LoginActivity
import kashyap.`in`.yajurvedaproject.utils.BiometricUtils
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.openQActivity
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SplashActivity : BaseActivity(), BiometricUtils.BiometricIntf {

    private var biometricUtils: BiometricUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
        biometricUtils = BiometricUtils(context, activity, this)
    }

    override fun onResume() {
        super.onResume()
        handleUserInfoIsStoredOrNot()
    }

    private fun handleUserInfoIsStoredOrNot() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && PrefUtils.hasQuarantineValue(context) && PrefUtils.isQuarantined(context)
            -> biometricUtils?.biometrics()
            PrefUtils.hasQuarantineValue(context) && !PrefUtils.isQuarantined(context)
            -> openQActivity(this, 3 * 1000)
            else -> Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }, 3 * 1000)
        }
    }

    private fun restartBiometrics() {
        finish()
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
    }

    override fun onLocationResult(location: Location?) {
    }

    override fun onAuthenticationSuccess() {
        openQActivity(activity, 300)
    }

    override fun onAuthenticationFailed() {
        restartBiometrics()
    }

    override fun noHardwareFound() {
        openQActivity(activity, 1300)
    }

    override fun biometricNotEnrolled() {
        openSettings()
    }

}
