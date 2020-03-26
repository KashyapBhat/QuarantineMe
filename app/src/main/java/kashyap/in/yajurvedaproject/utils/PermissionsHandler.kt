package kashyap.`in`.yajurvedaproject.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

import androidx.core.app.ActivityCompat

import java.util.ArrayList

import kashyap.`in`.yajurvedaproject.R

/**
 * Created by Kashyap Bhat on 2019-12-18.
 */
object PermissionsHandler {
    private val REQUEST_PERMISSION_MULTIPLE = 81
    private val TAG = PermissionsHandler::class.java.simpleName
    var isIsPermissionsChecksRunning = false
        private set
    private var isFirstTime = true
    private var dialog: AlertDialog? = null

    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    fun checkAndRequestPermissions(activity: Activity, permissions: Array<String>): Boolean {
        var permissionCount = 0
        var requestCount = 0
        val stringsThatNeedPermission = ArrayList<String>()
        for (permissionFor in permissions) {
            isIsPermissionsChecksRunning = true
            requestCount++
            val permissionStatus = ActivityCompat.checkSelfPermission(activity, permissionFor)
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                permissionCount++
                if (permissionCount == permissions.size) {
                    isFirstTime = false
                    isIsPermissionsChecksRunning = false
                    return true
                }
            } else
                stringsThatNeedPermission.add(permissionFor)
        }
        if (stringsThatNeedPermission.isNotEmpty() && (isFirstTime || showPermissionRationaleForAllPermissions(
                activity,
                stringsThatNeedPermission
            ))
        ) {
            ActivityCompat.requestPermissions(
                activity,
                stringsThatNeedPermission.toTypedArray(),
                REQUEST_PERMISSION_MULTIPLE
            )
        } else {
            checkAndAskPermsRx(activity, stringsThatNeedPermission)
        }
        if (requestCount == permissions.size) {
            isFirstTime = false
        }
        isIsPermissionsChecksRunning = false
        return false
    }

    private fun showPermissionRationaleForAllPermissions(
        activity: Activity,
        permissions: List<String>
    ): Boolean {
        for (permission in permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return false
        }
        return true
    }

    private fun checkAndAskPermsRx(activity: Activity, permissionFor: List<String>) {
        val permsGroupName = StringBuilder()
        for (permission in permissionFor) {
            try {
                val pm = activity.packageManager
                val permissionInfo = pm.getPermissionInfo(permission, 0)
                val groupInfo = pm.getPermissionGroupInfo(permissionInfo.group!!, 0)
                if (permsGroupName.isNotEmpty()) permsGroupName.append(" ,")
                permsGroupName.append(groupInfo.loadLabel(pm))
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, e.message, e)
            }

        }
        var msg = ""
        if (!TextUtils.isEmpty(permsGroupName)) {
            msg =
                String.format(activity.getString(R.string.open_settings), permsGroupName.toString())
        }
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.change_permissions)
            .setMessage(msg)
            .setPositiveButton(R.string.go_to_settings) { dialog, which ->
                dialog.dismiss()
                openSettingsActivity(activity)
            }.create()
        dialog!!.show()
    }

    private fun openSettingsActivity(context: Activity?) {
        if (context == null) {
            return
        }
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}
