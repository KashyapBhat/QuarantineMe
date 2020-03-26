package kashyap.`in`.yajurvedaproject.nonquarantine

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity

class NonQuarantineActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_non_quarantine)
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
    }

    override fun onLocationResult(location: Location?) {
    }
}
