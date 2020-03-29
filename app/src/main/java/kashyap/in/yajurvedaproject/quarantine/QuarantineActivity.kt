package kashyap.`in`.yajurvedaproject.quarantine

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.common.WEBVIEW_FRAGMENT
import kashyap.`in`.yajurvedaproject.info.InfoFragment
import kashyap.`in`.yajurvedaproject.notifications.NotificationFragment
import kashyap.`in`.yajurvedaproject.utils.FragmentInteractor
import kashyap.`in`.yajurvedaproject.webview.WebviewFragment
import kotlinx.android.synthetic.main.activity_quarantine.*

class QuarantineActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    FragmentInteractor {

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
        replaceFragment(this, QuarantinedHomeFragment.newInstance(quarantine), R.id.flContainer)
        // TODO: Save to firebase
        // Check with the older
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
                replaceFragment(
                    this,
                    QuarantinedHomeFragment.newInstance(quarantine),
                    R.id.flContainer
                )
                return true
            }
            R.id.qmmi2 -> {
                val fragment = InfoFragment.newInstance(quarantine)
                fragment.setFragmentInteractor(this)
                replaceFragment(
                    this,
                    fragment,
                    R.id.flContainer
                )
                return true
            }
            R.id.qmmi3 -> {
                replaceFragment(
                    this,
                    NotificationFragment.newInstance(quarantine),
                    R.id.flContainer
                )
                return true
            }
            R.id.qmmi4 -> {

                return true
            }
        }
        return false
    }

    override fun interact(from: Int, to: Int, value: Any?) {
        when (to) {
            WEBVIEW_FRAGMENT -> addFragment(
                this,
                WebviewFragment.newInstance(value as String),
                R.id.flContainer
            )
        }
    }
}
