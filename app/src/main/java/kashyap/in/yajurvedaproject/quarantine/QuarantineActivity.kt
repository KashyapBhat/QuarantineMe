package kashyap.`in`.yajurvedaproject.quarantine

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kotlinx.android.synthetic.main.activity_quarantine.*

class QuarantineActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quarantine)
        checkPermissionsAndRun()
        qmbottomNav.setOnNavigationItemSelectedListener(this)
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
        getLocation()
    }

    override fun onLocationResult(location: Location?) {
        hideProgress()
        replaceFragment(this, QuarantinedHomeFragment.newInstance(), R.id.flContainer)
        Toast.makeText(
            this,
            "Location ::::" + " Lat: " + location?.latitude + " Long: " + location?.longitude,
            Toast.LENGTH_LONG
        ).show()
        Log.d("Location ::::", " Lat: " + location?.latitude + "long: " + location?.longitude)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.qmmi1 -> {
                replaceFragment(this, QuarantinedHomeFragment.newInstance(), R.id.flContainer)
                return true
            }
            R.id.qmmi2 -> {

                return true
            }
            R.id.qmmi3 -> {

                return true
            }
            R.id.qmmi4 -> {

                return true
            }
        }
        return false
    }
}
