package kashyap.`in`.yajurvedaproject.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.QUARANTINE_DATA
import kashyap.`in`.yajurvedaproject.models.Notification
import kashyap.`in`.yajurvedaproject.models.Quarantine
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : BaseFragment() {
    private var quarantine: Quarantine? = null

    companion object {
        @JvmStatic
        fun newInstance(quarantine: Quarantine?) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(QUARANTINE_DATA, quarantine)
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
            quarantine = it.getParcelable(QUARANTINE_DATA)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvNotification?.layoutManager = LinearLayoutManager(getActivity())
        mAdapter = NotificationAdapter(quarantine?.notificationList ?: emptyList())
        rvNotification?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
        // TODO: Save to firebase
        // Get notifications data
    }

}
