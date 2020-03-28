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
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.userseperation.SeparationActivity
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SplashActivity : BaseActivity() {

    private var myBiometricPrompt: BiometricPrompt? = null
    private var promptInfo: PromptInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            PrefUtils.hasKey(this, IS_QUARANTINED)
        ) {
            biometrics()
        } else {
            Handler().postDelayed({ handleUserInfoIsStoredOrNot() }, 3 * 1000)
        }
    }

    private fun handleUserInfoIsStoredOrNot() {
        if (PrefUtils.hasKey(this, IS_QUARANTINED)) {
            GeneralUtils.handleQuarantinedOrNot(this)
        } else {
            startActivity(Intent(this, SeparationActivity::class.java))
            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun biometrics() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                handleUserInfoIsStoredOrNot()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showSnackBar("Please enable fingerprint / face detection before we go ahead",
                    "Go to settings",
                    Runnable { openSettings() })
            else -> checkBiometrics()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkBiometrics() {
        val newExecutor: Executor = Executors.newSingleThreadExecutor()
        if (myBiometricPrompt == null)
            myBiometricPrompt = getBiometricPrompt(newExecutor)
        if (promptInfo == null)
            promptInfo = PromptInfo.Builder()
                .setTitle("Biometric")
                .setSubtitle("Please use our fingerprint / face recognition")
                .setDescription("")
                .setNegativeButtonText("Cancel")
                .build()
        if (promptInfo != null)
            myBiometricPrompt?.authenticate(promptInfo!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getBiometricPrompt(newExecutor: Executor): BiometricPrompt? {
        return BiometricPrompt(this,
            newExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_NO_BIOMETRICS, BiometricPrompt.ERROR_UNABLE_TO_PROCESS, BiometricConstants.ERROR_HW_NOT_PRESENT ->
                            handleUserInfoIsStoredOrNot()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    runOnUiThread {
                        handleUserInfoIsStoredOrNot()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    runOnUiThread {
                        showSnackBar(
                            "Authentication error",
                            "Retry",
                            Runnable { restartBiometrics() })
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

}
