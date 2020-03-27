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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                biometrics()
            } else {
                handleUserInfoIsStoredOrNot()
            }
        }, 3 * 1000)
    }

    private fun handleUserInfoIsStoredOrNot() {
        if (PrefUtils.hasKey(this, IS_QUARANTINED)) {
            GeneralUtils.handleQuarantinedOrNot(this)
        } else {
            startActivity(Intent(this, SeparationActivity::class.java))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun biometrics() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                checkBiometrics()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                handleUserInfoIsStoredOrNot()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showSnackBar("Please enable fingerprint / face detection before we go ahead",
                    "Go to settings",
                    Runnable { openSettings() })
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkBiometrics() {
        val newExecutor: Executor = Executors.newSingleThreadExecutor()
        if (myBiometricPrompt == null)
            myBiometricPrompt = getBiometricPrompt(newExecutor)
        val promptInfo = PromptInfo.Builder()
            .setTitle("Biometric")
            .setSubtitle("Please use our fingerprint / face recognition")
            .setDescription("")
            .setNegativeButtonText("Cancel")
            .build()
        myBiometricPrompt?.authenticate(promptInfo)
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
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON, BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_CANCELED ->
                            showSnackBar(
                                "Please use biometrics to get inside the app",
                                "Okay",
                                Runnable { checkBiometrics() })
                        BiometricPrompt.ERROR_NO_BIOMETRICS, BiometricPrompt.ERROR_UNABLE_TO_PROCESS, BiometricConstants.ERROR_HW_NOT_PRESENT ->
                            handleUserInfoIsStoredOrNot()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    handleUserInfoIsStoredOrNot()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showSnackBar(
                        "Authentication error",
                        "Retry",
                        Runnable { checkBiometrics() })
                }
            })
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
    }

    override fun onLocationResult(location: Location?) {
    }

}
