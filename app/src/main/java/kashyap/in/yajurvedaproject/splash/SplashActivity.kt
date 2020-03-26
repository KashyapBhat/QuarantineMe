package kashyap.`in`.yajurvedaproject.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.userseperation.SeparationActivity
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
        handleUserInfoIsStoredOrNot()
    }

    private fun handleUserInfoIsStoredOrNot() {
        if (PrefUtils.hasKey(this, IS_QUARANTINED)) {
            GeneralUtils.handleQuarantinedOrNot(this)
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, SeparationActivity::class.java))
                finish()
            }, 2 * 1000)
        }
    }
}
