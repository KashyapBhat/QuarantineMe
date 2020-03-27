package kashyap.`in`.yajurvedaproject.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*
import kotlin.concurrent.schedule
import androidx.core.content.ContextCompat.startActivity
import kashyap.`in`.yajurvedaproject.BuildConfig
import android.R
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import kashyap.`in`.yajurvedaproject.base.BaseActivity
import kashyap.`in`.yajurvedaproject.base.BaseFragment
import kashyap.`in`.yajurvedaproject.common.IS_QUARANTINED
import kashyap.`in`.yajurvedaproject.nonquarantine.NonQuarantineActivity
import kashyap.`in`.yajurvedaproject.quarantine.QuarantineActivity


/**
 * Created by Kashyap Bhat on 2019-12-14.
 */

class GeneralUtils {

    companion object {

        fun updateUppFromPlaystore(context: Context) {
            val appName = BuildConfig.APPLICATION_ID
            showDialogWithButtons(
                context,
                String.format(
                    context.getString(kashyap.`in`.yajurvedaproject.R.string.please_update_app),
                    appName
                ),
                String.format(
                    context.getString(kashyap.`in`.yajurvedaproject.R.string.please_update_app_desc),
                    appName
                ),
                context.getString(kashyap.`in`.yajurvedaproject.R.string.update), "", Runnable {
                    try {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$context.packageName")
                            )
                        )
                    } catch (ex: android.content.ActivityNotFoundException) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$context.packageName")
                            )
                        )
                    }
                }, null
                , false
            )
        }

        fun showDialogWithButtons(
            context: Context,
            title: String,
            desc: String,
            btLeft: String,
            btRight: String,
            leftButtonRunnable: Runnable?,
            rightButtonRunnable: Runnable?,
            cancelable: Boolean
        ) {
            val dialog = Dialog(context)

            dialog.setContentView(kashyap.`in`.yajurvedaproject.R.layout.custom_dialog)
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(cancelable)


            val tvTitle =
                dialog.findViewById<TextView>(kashyap.`in`.yajurvedaproject.R.id.tvDialogTitle)
            tvTitle.visibility = if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
            tvTitle.text = title

            val tvDescription =
                dialog.findViewById<TextView>(kashyap.`in`.yajurvedaproject.R.id.tvDialogContent)
            tvDescription.visibility = if (TextUtils.isEmpty(desc)) View.GONE else View.VISIBLE
            tvDescription.text = desc

            val btnNo = dialog.findViewById<Button>(kashyap.`in`.yajurvedaproject.R.id.btnOne)
            btnNo.text = btLeft
            btnNo.visibility = if (TextUtils.isEmpty(btLeft)) View.GONE else View.VISIBLE
            btnNo.setOnClickListener {
                dialog.dismiss()
                leftButtonRunnable?.run()
            }

            val btnYes = dialog.findViewById<Button>(kashyap.`in`.yajurvedaproject.R.id.btnTwo)
            btnYes.text = btRight
            btnYes.visibility = if (TextUtils.isEmpty(btRight)) View.GONE else View.VISIBLE
            btnYes.setOnClickListener {
                dialog.dismiss()
                rightButtonRunnable?.run()
            }

            dialog.show()
        }

        fun slideUp(view: View) {
            view.visibility = View.VISIBLE
            val animate = TranslateAnimation(
                0f, // fromXDelta
                0f, // toXDelta
                view.height.toFloat(), // fromYDelta
                0f
            )                // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
        }

        fun slideDown(view: View) {
            val animate = TranslateAnimation(
                0f, // fromXDelta
                0f, // toXDelta
                0f, // fromYDelta
                view.height.toFloat()
            ) // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
            view.visibility = View.GONE
        }

        fun changeButtonPosition(
            context: Context?,
            button: View,
            bottomMarginInDp: Int
        ) {
            val params: RelativeLayout.LayoutParams =
                button.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(
                0,
                0,
                0,
                getPxFromDp(context, bottomMarginInDp)
            )
            button.layoutParams = params
        }

        fun getPxFromDp(context: Context?, dp: Int): Int {
            val r = context?.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r?.displayMetrics
            ).toInt()
        }

        fun getPermissionRequired(): List<String> {
            return listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

        fun shareApp(activity: Activity) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App")
                var shareMessage =
                    "\nHey! Recommending you this application, check out the application here: \n\n"
                shareMessage =
                    shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                activity.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }

        }

        public fun createNotification(context: Context, title: String, desc: String, icon: Int) {
            val emptyIntent = Intent()
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                emptyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setChannelId("asas")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.notify(0, mBuilder.build())
        }

        fun transact(
            activity: BaseActivity?,
            baseFragment: BaseFragment?,
            shouldReplace: Boolean?, @IdRes containerId: Int
        ) {
            if (containerId == -1) {
                return
            }
            if (shouldReplace == true) {
                activity?.supportFragmentManager?.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            val transaction =
                activity?.supportFragmentManager?.beginTransaction()
            baseFragment?.let { transaction?.replace(containerId, it)?.addToBackStack(null) }
            transaction?.commit()
        }

        fun handleQuarantinedOrNot(context: Context) {
            if (PrefUtils.getFromPrefs(context, IS_QUARANTINED, false) as Boolean) {
                context.startActivity(Intent(context, QuarantineActivity::class.java))
            } else {
                context.startActivity(Intent(context, NonQuarantineActivity::class.java))
            }
        }
    }
}