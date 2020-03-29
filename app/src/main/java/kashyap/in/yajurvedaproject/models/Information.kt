package kashyap.`in`.yajurvedaproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Information(
    val header: String?,
    val desc: String?,
    val imageUrl: String?,
    val linkUrl: String?
) : Parcelable