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
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils.Companion.transact


abstract class BaseFragment : Fragment() {

    protected var listener: OnFragmentInteractionListener? = null
    protected var activity: Activity? = null

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
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener && activity == null && context is Activity) {
            listener = context
            activity = context as Activity
        } else {
            Log.d("Exception: ", "$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        activity = null
    }

    interface OnFragmentInteractionListener {
        fun showOrHideOptions()
    }

    fun showProgress() {
        if (activity != null && activity is BaseActivity)
            (activity as BaseActivity).showProgress()
    }

    fun hideProgress() {
        if (activity != null && activity is BaseActivity)
            (activity as BaseActivity).hideProgress()
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
