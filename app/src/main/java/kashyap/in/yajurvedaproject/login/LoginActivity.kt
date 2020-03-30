package kashyap.`in`.yajurvedaproject.login

import android.content.Intent
import android.location.Location
import android.os.Bundle
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        hideToolbar()
        buttonContinue?.setOnClickListener {
            loginButtonPressed()
        }
    }

    private fun loginButtonPressed() {
        val mobile: String = editTextMobile.text.toString().trim()
        if (mobile.isEmpty() || mobile.length < 10) {
            editTextMobile.error = "Please, Enter a valid mobile"
            editTextMobile.requestFocus()
            return
        }
        val intent = Intent(context, ValidateLoginActivity::class.java)
        intent.putExtra("mobile", mobile)
        startActivity(intent)
    }

    override fun networkChanged() {

    }

    override fun onAllPermissionsAcquired() {
    }

    override fun onLocationResult(location: Location?) {
    }

}
