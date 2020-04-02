package kashyap.`in`.yajurvedaproject.userseperation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashyap.`in`.yajurvedaproject.BuildConfig
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.IS_DOCTOR
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_success.*

class SuccessFragment : BaseFragment() {
    private var url: String = ""

    companion object {
        @JvmStatic
        fun newInstance() =
            SuccessFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_success, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val string = "You have successfully completed your on-boarding process."
        val text = if (BuildConfig.FLAVOR == "ema") string
        else string.plus(
            GeneralUtils.getAddressFromLocation(
                context,
                (getActivity() as BaseActivity?)?.lastKnownLocation
            )
        ).plus("\n(No need to worry, address will be only be accessed if needed only by health administrators.)")
        tvSuccessDesc?.text = text
        btSuccess.setOnClickListener {
            getActivity()?.let { it1 -> GeneralUtils.openQActivity(it1, 0) }
        }
        ivCross.setOnClickListener {
            getActivity()?.let { it1 -> GeneralUtils.openQActivity(it1, 0) }
        }
    }

    override fun afterFBDataFetch() {
    }

    override fun showProgress() {

    }

    override fun hideProgress() {
    }
}