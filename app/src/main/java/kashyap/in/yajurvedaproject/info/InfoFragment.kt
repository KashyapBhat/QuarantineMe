package kashyap.`in`.yajurvedaproject.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.INFO_FRAGMENT
import kashyap.`in`.yajurvedaproject.common.QUARANTINE_DATA
import kashyap.`in`.yajurvedaproject.common.WEBVIEW_FRAGMENT
import kashyap.`in`.yajurvedaproject.models.Information
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kashyap.`in`.yajurvedaproject.utils.FragmentInteractor
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : BaseFragment(), InfoAdapter.InfoItemClickIntf {
    private var quarantine: Quarantine? = null
    private var fragmentInteractor: FragmentInteractor? = null

    companion object {
        @JvmStatic
        fun newInstance(quarantine: Quarantine?) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(QUARANTINE_DATA, quarantine)
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
            quarantine = it.getParcelable(QUARANTINE_DATA)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoAdapter = InfoAdapter(quarantine?.informationList ?: emptyList(), getActivity(), this)
        rvInfo?.adapter = infoAdapter
        rvInfo?.layoutManager = LinearLayoutManager(getActivity())
        infoAdapter?.notifyDataSetChanged()
        // TODO: Save to firebase
        // Get information from firebase
    }

    fun setFragmentInteractor(fragmentInteractor: FragmentInteractor?) {
        this.fragmentInteractor = fragmentInteractor
    }

    override fun onItemClick(information: Information?) {
        fragmentInteractor?.interact(INFO_FRAGMENT, WEBVIEW_FRAGMENT, information?.linkUrl)
    }

}
