package kashyap.`in`.yajurvedaproject.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BiometricUtils(
    val context: Context?,
    val activity: BaseActivity?,
    val bioIntf: BiometricIntf?
) {

    private var myBiometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun biometrics() {
        val biometricManager = context?.let { BiometricManager.from(it) }
        when (biometricManager?.canAuthenticate()) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                bioIntf?.noHardwareFound()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                activity?.showSnackBar("Please enable fingerprint / face detection before we go ahead",
                    "Go to settings",
                    Runnable { bioIntf?.biometricNotEnrolled() })
            else -> checkBiometrics()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkBiometrics() {
        val newExecutor: Executor = Executors.newSingleThreadExecutor()
        if (myBiometricPrompt == null)
            myBiometricPrompt = getBiometricPrompt(newExecutor)
        if (promptInfo == null)
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric")
                .setSubtitle("Please use your fingerprint / face recognition")
                .setDescription("")
                .setNegativeButtonText("Cancel")
                .build()
        if (promptInfo != null)
            myBiometricPrompt?.authenticate(promptInfo!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getBiometricPrompt(newExecutor: Executor): BiometricPrompt? {
        return activity?.let {
            BiometricPrompt(
                it,
                newExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        when (errorCode) {
                            BiometricPrompt.ERROR_NO_BIOMETRICS, BiometricPrompt.ERROR_UNABLE_TO_PROCESS, BiometricConstants.ERROR_HW_NOT_PRESENT ->
                                bioIntf?.noHardwareFound()
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        activity.runOnUiThread {
                            bioIntf?.onAuthenticationSuccess()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        activity.runOnUiThread {
                            activity.showSnackBar(
                                "Authentication error",
                                "Retry",
                                Runnable { bioIntf?.onAuthenticationFailed() })
                        }
                    }
                })
        }
    }

    interface BiometricIntf {
        fun onAuthenticationSuccess()
        fun onAuthenticationFailed()
        fun noHardwareFound()
        fun biometricNotEnrolled()
    }
}