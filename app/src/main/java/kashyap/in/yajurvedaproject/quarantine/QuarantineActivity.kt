package kashyap.`in`.yajurvedaproject.quarantine

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kashyap.`in`.yajurvedaproject.ProfileFragment
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.common.APP_LAST_USE
import kashyap.`in`.yajurvedaproject.common.USER_NAME
import kashyap.`in`.yajurvedaproject.common.USER_PHONE_NUMBER
import kashyap.`in`.yajurvedaproject.common.WEBVIEW_FRAGMENT
import kashyap.`in`.yajurvedaproject.info.InfoFragment
import kashyap.`in`.yajurvedaproject.notifications.NotificationFragment
import kashyap.`in`.yajurvedaproject.utils.FragmentInteractor
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.getAddressFromLocation
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kashyap.`in`.yajurvedaproject.webview.WebviewFragment
import kotlinx.android.synthetic.main.activity_quarantine.*

class QuarantineActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    FragmentInteractor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quarantine)
        showProgress()
        checkPermissionsAndRun()
        qmbottomNav.setOnNavigationItemSelectedListener(this)
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
        getLocation()
    }

    override fun onLocationResult(location: Location?) {
        lastKnownLocation = location
        storeUserLastOpened(location)
        replaceFragment(this, QuarantinedHomeFragment.newInstance(), R.id.flContainer)
        getAddressFromLocation(context, location)
        Log.d("Location ::::", " Lat: " + location?.latitude + "long: " + location?.longitude)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.qmmi1 -> {
                showProgress()
                replaceFragment(
                    this,
                    QuarantinedHomeFragment.newInstance(),
                    R.id.flContainer
                )
                return true
            }
            R.id.qmmi2 -> {
                val fragment = InfoFragment.newInstance()
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
                    NotificationFragment.newInstance(),
                    R.id.flContainer
                )
                return true
            }
            R.id.qmmi4 -> {
                replaceFragment(
                    this,
                    ProfileFragment.newInstance(),
                    R.id.flContainer
                )
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

    private fun storeUserLastOpened(location: Location?) {
        val user = hashMapOf(
            "LastAppOpenedTime" to "Date: ".plus(GeneralUtils.getCurrentDate()).plus(" Time: ").plus(
                GeneralUtils.getCurentTime()
            ),
            "LastAppOpenedStamp" to FieldValue.serverTimestamp(),
            "LastLocation" to GeoPoint(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
            "LastAddress" to GeneralUtils.getAddressFromLocation(context, location),
            USER_PHONE_NUMBER to PrefUtils.userId(context),
            USER_NAME to PrefUtils.getFromPrefs(context, USER_NAME, "")
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(PrefUtils.userId(context))
            .document(APP_LAST_USE)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

}
