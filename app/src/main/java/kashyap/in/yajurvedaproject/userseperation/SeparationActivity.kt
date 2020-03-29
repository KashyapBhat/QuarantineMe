package kashyap.`in`.yajurvedaproject.userseperation

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.common.LATITUDE
import kashyap.`in`.yajurvedaproject.common.LONGITUDE
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
            "Consider the current location as your home location?",
            "We need the location where you will be staying quarantined.",
            "No",
            "Sure",
            Runnable { askToIsolate() },
            Runnable { getLocation() },
            false
        )

    }

    private fun askToIsolate() {
        showSnackBar(
            "Please come back after you isolated / quarantine yourself", "Okay", null
        )
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
        saveUserDataToPrefs(location)
    }

    private fun saveUserDataToPrefs(location: Location?) {
        PrefUtils.saveToPrefs(context, IS_QUARANTINED, isQuarantined)
        PrefUtils.saveToPrefs(context, LATITUDE, location?.latitude)
        PrefUtils.saveToPrefs(context, LONGITUDE, location?.longitude)
        saveToFirebase()
        handleNextScreen()
    }

    private fun saveToFirebase() {
        // TODO: Save to firebase
        // save locations and everything
    }

    private fun handleNextScreen() {
        rlQALayout.visibility = View.GONE
        flContainer.visibility = View.VISIBLE
        replaceFragment(this, SuccessFragment.newInstance(), R.id.flContainer)
    }
}
