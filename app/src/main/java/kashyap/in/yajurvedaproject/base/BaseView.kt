package kashyap.`in`.yajurvedaproject.base

/**
 * Created by Kashyap Bhat on 2019-12-18.
 */
interface BaseView {
    fun showLoading()
    fun hideLoading()
    fun showToast(title: String, actionText: String, runnable: Runnable?)
}