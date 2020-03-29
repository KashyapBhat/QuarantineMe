package kashyap.`in`.yajurvedaproject.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private var mAdapter: NotificationAdapter? = null

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvNotification?.layoutManager = LinearLayoutManager(getActivity())
        mAdapter = NotificationAdapter(getNotificationAdapter())
        rvNotification?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun getNotificationAdapter(): List<Notification?>? {
        return listOf(
            Notification(
                "Identification and isolation of persons with infection. ",
                "House to house survey around 3 kms radius of house of primary positive case for identification of any symptoms / travel history / known contact history for COVID 19. "
            ),
            Notification(
                "Identification of high-risk contacts for quarantine. ",
                "All the symptomatic individuals to be screened by physician and if required the sample will be collected from their home or in the designated centre. "
            ),
            Notification(
                "Sanitization of quarantine places for identified positive cases",
                "Patients will be sent to the designated centre by ambulance or designated vehicle.. "
            ),
            Notification(
                "Ensurement of surveillance for societies with RWA. ",
                "Team will look out for local health care facilities of the area (Private clinic, Moholla clinic etc) "
            ),
            Notification(
                "Identification and isolation of persons with infection. ",
                "House to house survey around 3 kms radius of house of primary positive case for identification of any symptoms / travel history / known contact history for COVID 19. "
            ),
            Notification(
                "Identification of high-risk contacts for quarantine. ",
                "All the symptomatic individuals to be screened by physician and if required the sample will be collected from their home or in the designated centre. "
            ),
            Notification(
                "Sanitization of quarantine places for identified positive cases",
                "Patients will be sent to the designated centre by ambulance or designated vehicle.. "
            ),
            Notification(
                "Ensurement of surveillance for societies with RWA. ",
                "Team will look out for local health care facilities of the area (Private clinic, Moholla clinic etc) "
            ),
            Notification(
                "Identification and isolation of persons with infection. ",
                "House to house survey around 3 kms radius of house of primary positive case for identification of any symptoms / travel history / known contact history for COVID 19. "
            ),
            Notification(
                "Identification of high-risk contacts for quarantine. ",
                "All the symptomatic individuals to be screened by physician and if required the sample will be collected from their home or in the designated centre. "
            ),
            Notification(
                "Sanitization of quarantine places for identified positive cases",
                "Patients will be sent to the designated centre by ambulance or designated vehicle.. "
            ),
            Notification(
                "Ensurement of surveillance for societies with RWA. ",
                "Team will look out for local health care facilities of the area (Private clinic, Moholla clinic etc) "
            )
        )
    }

}
