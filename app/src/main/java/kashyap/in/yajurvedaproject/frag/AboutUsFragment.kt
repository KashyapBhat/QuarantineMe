package kashyap.`in`.yajurvedaproject.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.OPEN_URL


class AboutUsFragment : BaseFragment() {
    private var url: String = ""

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            AboutUsFragment().apply {
                arguments = Bundle().apply {
                    putString(OPEN_URL, param1)
                }
            }
    }

    override fun onCreateViewSetter(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_show_pdf, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(OPEN_URL).orEmpty()
        }
    }

}
