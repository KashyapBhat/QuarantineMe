package kashyap.`in`.yajurvedaproject.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.QUARANTINE_DATA
import kashyap.`in`.yajurvedaproject.models.Information
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : BaseFragment() {
    private var quarantine: Quarantine? = null

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
        infoAdapter = InfoAdapter(quarantine?.informationList, getActivity())
        rvInfo?.adapter = infoAdapter
        rvInfo?.layoutManager = LinearLayoutManager(getActivity())
        infoAdapter?.notifyDataSetChanged()
        // TODO: Save to firebase
        // Get information from firebase
    }

    private fun getInfoList(): List<Information?>? {
        return listOf(
            Information(
                "Helpline Numbers of States & Union Territories (UTs)",
                "Click here to see central Helpline Number for corona-virus.",
                "https://img.etimg.com/thumb/msid-74706972,width-300,imgsize-329023,resizemode-4/india-considers-easier-loan-tax-rules-to-help-economy-endure-coronavirus-pain.jpg",
                ""
            ),
            Information(
                "State/District wise details of Positive cases of Covid19",
                "Likely to change once districts of all known 724 cases are ascertained, The details of the districts are being ascertained. Total District affected - 132",
                "",
                ""
            ),
            Information(
                "The best way to prevent illness is to avoid being exposed to this virus.",
                "* Take steps to protect yourself\n* Take steps to protect others\n* Clean and disinfect",
                "data:image/jpeg;base64,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",
                ""
            ),
            Information(
                "Handwashing: Clean Hands Save Lives",
                "Regular handwashing is one of the best ways to remove germs, avoid getting sick, and prevent the spread of germs to others. Whether you are at home, at work, traveling, or out in the community, find out how handwashing with soap and water can protect you and your family.",
                "https://www.cdc.gov/handwashing/images/HH-Eng-Family-FB-1200x675.jpg",
                ""
            )
        )
    }

}
