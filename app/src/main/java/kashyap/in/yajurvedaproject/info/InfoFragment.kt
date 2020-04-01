package kashyap.`in`.yajurvedaproject.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.INFO_FRAGMENT
import kashyap.`in`.yajurvedaproject.common.WEBVIEW_FRAGMENT
import kashyap.`in`.yajurvedaproject.models.Information
import kashyap.`in`.yajurvedaproject.utils.FragmentInteractor
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : BaseFragment(), InfoAdapter.InfoItemClickIntf {
    private var fragmentInteractor: FragmentInteractor? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            InfoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    var infoAdapter: InfoAdapter? = null

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        srInfo?.setOnRefreshListener {
            getQuarantineDataFromFb()
        }
    }

    override fun afterFBDataFetch() {
        tvActiveValue?.text = quarantine?.activeCase
        tvCuredValue?.text = quarantine?.curedCase
        tvDeathValue?.text = quarantine?.deathCase
        infoAdapter = InfoAdapter(quarantine?.informationList ?: emptyList(), getActivity(), this)
        rvInfo?.adapter = infoAdapter
        rvInfo?.layoutManager = LinearLayoutManager(getActivity())
        infoAdapter?.notifyDataSetChanged()
        // TODO: Save to firebase
        // Get information from firebase
    }

    override fun showProgress() {
        srInfo?.isRefreshing = true
    }

    override fun hideProgress() {
        srInfo?.isRefreshing = false
    }

    fun setFragmentInteractor(fragmentInteractor: FragmentInteractor?) {
        this.fragmentInteractor = fragmentInteractor
    }

    override fun onItemClick(information: Information?) {
        if (information?.linkUrl?.isNotEmpty() == true && information.linkUrl.isNotBlank())
            fragmentInteractor?.interact(INFO_FRAGMENT, WEBVIEW_FRAGMENT, information.linkUrl)
    }

}
