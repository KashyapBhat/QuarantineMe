package kashyap.`in`.yajurvedaproject.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.transact


abstract class BaseFragment : Fragment() {

    protected var activity: Activity? = null
    protected var quarantine: Quarantine? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return onCreateViewSetter(inflater, container, savedInstanceState)
    }

    abstract fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
        getQuarantineDataFromFb()
    }

    fun getQuarantineDataFromFb() {
        showProgress()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("quarantine")
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value: HashMap<*, *>? = dataSnapshot.value as HashMap<*, *>?
                quarantine = getJsonFromHashmap(value)
                afterFBDataFetch()
                hideProgress()
            }

            override fun onCancelled(error: DatabaseError) {
                hideProgress()
                showSnackBar(
                    "Something went wrong",
                    "Retry",
                    Runnable { getQuarantineDataFromFb() })
                Log.w("", "Failed to read value.", error.toException())
            }
        })
    }

    abstract fun afterFBDataFetch()

    private fun getJsonFromHashmap(value: HashMap<*, *>?): Quarantine {
        val gson = Gson()
        val jsonElement = gson.toJsonTree(value)
        return gson.fromJson(jsonElement, Quarantine::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity == null && context is Activity) {
            activity = getActivity() as Activity
        } else {
            Log.d("Exception: ", "$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    abstract fun showProgress()

    abstract fun hideProgress()

    fun showSnackBar(title: String, actionText: String, runnable: Runnable?) {
        getActivity()?.window?.let { GeneralUtils.showSnackBar(title, it, actionText, runnable) }
    }

    fun addFragment(activity: BaseActivity, baseFragment: BaseFragment?, @IdRes containerId: Int) {
        transact(activity, baseFragment, false, containerId)
    }

    fun replaceFragment(
        activity: BaseActivity,
        baseFragment: BaseFragment?, @IdRes containerId: Int
    ) {
        transact(activity, baseFragment, true, containerId)
    }
}
