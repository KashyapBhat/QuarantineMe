package kashyap.`in`.yajurvedaproject.login

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.userseperation.SeparationActivity
import kotlinx.android.synthetic.main.activity_validate_login.*
import java.util.concurrent.TimeUnit

class ValidateLoginActivity : BaseActivity() {

    private var mVerificationId: String? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_login)
        hideToolbar()
        mAuth = FirebaseAuth.getInstance()
        val intent = intent
        val mobile = intent.getStringExtra("mobile")
        sendVerificationCode(mobile)
    }

    private fun sendVerificationCode(mobile: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$mobile",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
        btSignIn?.setOnClickListener {
            verifyButtonClicked()
        }
    }

    private val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    editTextMobile.setText(code)
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                mVerificationId = s
            }
        }

    private fun verifyButtonClicked() {
        val code: String = editTextMobile.text.toString().trim()
        if (code.isEmpty() || code.length < 6) {
            editTextMobile.error = "Enter valid code"
            editTextMobile.requestFocus()
            return
        }
        verifyVerificationCode(code)
    }


    private fun verifyVerificationCode(otp: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId ?: "", otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    onVerificationSuccessful(user)
                } else {
                    var message =
                        "Somthing is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }
                    showSnackBar(message, "Okay", null)
                }
            }
    }

    private fun onVerificationSuccessful(user: FirebaseUser?) {
        startActivity(Intent(this, SeparationActivity::class.java))
        finish()
        overridePendingTransition(R.anim.enter, R.anim.exit)
        Log.d(
            "User Login successful",
            "Verified Successfully." + user?.uid + user?.phoneNumber + user?.displayName
        )
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
    }

    override fun onLocationResult(location: Location?) {
    }

}
