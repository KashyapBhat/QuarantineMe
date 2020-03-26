package kashyap.`in`.yajurvedaproject.userseperation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.common.LATITUDE
import kashyap.`in`.yajurvedaproject.common.LONGITUDE
import kashyap.`in`.yajurvedaproject.nonquarantine.NonQuarantineActivity
import kashyap.`in`.yajurvedaproject.quarantine.QuarantineActivity
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.activity_separation.*

class SeparationActivity : BaseActivity() {

    var isQuarantined: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_separation)
        btQuarantine.setOnClickListener {
            isQuarantined = true
            checkPermissionsAndRun()
        }
        btIsolated.setOnClickListener {
            checkPermissionsAndRun()
        }
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
        GeneralUtils.showDialogWithButtons(
            context,
            "Is this where you will be staying isolated?",
            "",
            "No",
            "Yes",
            Runnable { askToIsolate() },
            Runnable { getLocation() },
            false
        )

    }

    private fun askToIsolate() {
        showSnackBar(
            "Please come back after you isolated yourself", "Okay", null
        )
    }

    override fun onLocationResult(location: Location?) {
        saveUserDataToPrefs(location)
        handleNextScreen()
    }

    private fun saveUserDataToPrefs(location: Location?) {
        PrefUtils.saveToPrefs(context, IS_QUARANTINED, isQuarantined)
        PrefUtils.saveToPrefs(context, LATITUDE, location?.latitude)
        PrefUtils.saveToPrefs(context, LONGITUDE, location?.longitude)
    }

    private fun handleNextScreen() {
        replaceFragment(this, SuccessFragment.newInstance(), R.id.flContainer)
    }
}
