package kashyap.`in`.yajurvedaproject.models

import android.os.Parcelable
import android.telephony.emergency.EmergencyNumber
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quarantine(
    val activeCase: String?,
    val curedCase: String?,
    val deathCase: String?,
    val informationList: List<Information?>?,
    val notificationList: List<Notification?>?,
    val xMins: Long? = 15,
    val emergencyNumber: String?
) : Parcelable