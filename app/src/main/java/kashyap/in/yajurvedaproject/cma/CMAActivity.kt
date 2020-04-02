package kashyap.`in`.yajurvedaproject.cma

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kotlinx.android.synthetic.main.activity_c_m_a.*

class CMAActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_m_a)
        btGetPatient?.setOnClickListener { getUserDataFromFB() }
    }

    private fun getUserDataFromFB() {
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("+91".plus(etMobile?.text?.toString()) ?: "")
        docRef.get()
            .addOnSuccessListener { documentList ->
                if (documentList != null) {
                    Log.d("", "DocumentSnapshot data: ${documentList.documents}")
                    container?.visibility = View.GONE
                    btGetPatient?.visibility = View.GONE
                    var data: String? = ""
                    documentList.documents.forEach {
                        data = data.plus(
                            (it.data?.keys?.toString()
                                ?: "").plus("\t").plus(it.data?.values?.toString() ?: "").plus("\n")
                        )
                    }
                    tvExplain.text = data
                } else {
                    Log.d("", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("", "get failed with ", exception)
            }


    }

    override fun networkChanged() {

    }

    override fun onAllPermissionsAcquired() {

    }

    override fun onLocationResult(location: Location?) {

    }
}
