package kashyap.`in`.yajurvedaproject.quarantine

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity

class QuarantineActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quarantine)
        checkPermissionsAndRun()
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
        getLocation()
    }

    override fun onLocationResult(location: Location?) {
        hideProgress()
        if (location == null)
            Toast.makeText(
                this,
                "We are .....",
                Toast.LENGTH_LONG
            ).show()
        Toast.makeText(
            this,
            "Location ::::" + " Lat: " + location?.latitude + "long: " + location?.longitude,
            Toast.LENGTH_LONG
        ).show()
        Log.d("Location ::::", " Lat: " + location?.latitude + "long: " + location?.longitude)
    }
}
