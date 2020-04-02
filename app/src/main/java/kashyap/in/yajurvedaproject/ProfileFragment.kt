package kashyap.`in`.yajurvedaproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.*
import kashyap.`in`.yajurvedaproject.utils.PrefUtils
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getValuesFromFB()
        enrollNow?.setOnClickListener { saveValues() }
    }

    private fun getValuesFromFB() {
        etName?.setText(PrefUtils.getFromPrefs(context, USER_NAME, "") as String)
        etEmail?.setText(PrefUtils.getFromPrefs(context, USER_EMAIL, "") as String)
        etPhone?.setText(PrefUtils.userId(context))
        etAddress?.setText(PrefUtils.getFromPrefs(context, HOME_ADDRESS, "") as String)
    }

    private fun saveValues() {
        val userName = etName?.text?.toString()?.trim()
        val userEmail = etEmail?.text?.toString()?.trim()
        val userAddress = etAddress?.text?.toString()?.trim()
        if (userName?.isEmpty() == true || userEmail?.isEmpty() == true || userAddress?.isEmpty() == true) {
            showSnackBar("Please fill all the values to proceed.", "Okay", null)
            return
        }
        PrefUtils.saveToPrefs(context, USER_NAME, userName)
        PrefUtils.saveToPrefs(context, USER_EMAIL, userEmail)
        PrefUtils.saveToPrefs(context, HOME_ADDRESS, userAddress)
        saveToFirebase(userName, userEmail, userAddress)
    }

    private fun saveToFirebase(userName: String?, userEmail: String?, userAddress: String?) {
        val init = hashMapOf(
            USER_NAME to userName,
            USER_EMAIL to userEmail,
            HOME_ADDRESS to userAddress,
            USER_PHONE_NUMBER to PrefUtils.userId(context)
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(PrefUtils.userId(context))
            .document(PROFILE_DOC)
            .set(init)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                if (PrefUtils.isQuarantined(context)) {
                    showSnackBar("Data is sent to the admin for verification.", "Great", null)
                } else {
                    showSnackBar("Data saved successfully", "Great", null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                showSnackBar("Something went wrong", "Retry", null)
            }
    }

    override fun afterFBDataFetch() {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

}
