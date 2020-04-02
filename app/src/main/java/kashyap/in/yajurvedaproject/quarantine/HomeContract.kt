package kashyap.`in`.yajurvedaproject.quarantine

interface HomeContract {
    interface view {
        fun handleImageButton(isEnabled: Boolean)
        fun handleIssuesButton(isEnabled: Boolean)
        fun showToast(title: String, actionTitle: String)
        fun showHomeSnackBar(title: String, actionTitle: String, runnable: Runnable?)
    }
}