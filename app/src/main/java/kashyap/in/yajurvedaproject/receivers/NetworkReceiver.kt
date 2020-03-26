package kashyap.`in`.yajurvedaproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService

/**
 * Created by Kashyap Bhat on 2019-12-14.
 */

class NetworkReceiver : BroadcastReceiver() {

    private lateinit var networkChangeListener: NetworkChangeListener

    override fun onReceive(context: Context?, p1: Intent?) {
        networkChangeListener.onNetworkChanged()
    }

    fun setNetworkChangeListener(networkChangeListener: NetworkChangeListener) {
        this.networkChangeListener = networkChangeListener
    }

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    interface NetworkChangeListener {
        fun onNetworkChanged()
    }
}