package kashyap.`in`.yajurvedaproject.userseperation

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.common.HOME_LOCATION
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.common.LATITUDE
import kashyap.`in`.yajurvedaproject.common.LONGITUDE
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.activity_separation.*
import kotlinx.android.synthetic.main.fragment_quarantined_home.*

class SeparationActivity : BaseActivity() {

    var isQuarantined: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_separation)
        hideToolbar()
        setButtons()
    }

    private fun setButtons() {
        btQuarantine.setOnClickListener {
            isQuarantined = true
            checkPermissionsAndRun()
        }
        btIsolated.setOnClickListener {
            isQuarantined = false
            checkPermissionsAndRun()
        }
    }

    override fun networkChanged() {
    }

    override fun onAllPermissionsAcquired() {
        GeneralUtils.showDialogWithButtons(
            context,
            "Consider current location as your home location?",
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
        Toast.makeText(
            this, "" + " Lat: " + location?.latitude + "long: " + location?.longitude,
            Toast.LENGTH_SHORT
        ).show()
        Log.d("Location ::::", " Lat: " + location?.latitude + "long: " + location?.longitude)
        saveUserDataToPrefs(location)
    }

    private fun saveUserDataToPrefs(location: Location?) {
        PrefUtils.saveToPrefs(context, IS_QUARANTINED, isQuarantined)
        PrefUtils.saveToPrefs(context, LATITUDE, location?.latitude)
        PrefUtils.saveToPrefs(context, LONGITUDE, location?.longitude)
        saveToFirebase(location)
        handleNextScreen()
    }

    private fun saveToFirebase(location: Location?) {
        val init = hashMapOf(
            HOME_LOCATION to GeoPoint(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
            IS_QUARANTINED to isQuarantined
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(PrefUtils.userId(context))
            .document("1Init")
            .set(init)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                etTempValue?.setText("")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                showSnackBar("Something went wrong", "Retry", Runnable { setButtons() })
            }
    }

    private fun handleNextScreen() {
        rlQALayout.visibility = View.GONE
        flContainer.visibility = View.VISIBLE
        replaceFragment(this, SuccessFragment.newInstance(), R.id.flContainer)
    }
}
