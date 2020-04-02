package kashyap.in.yajurvedaproject.quarantine.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kashyap.in.yajurvedaproject.R;
import kashyap.in.yajurvedaproject.utils.GeneralUtils;
import kashyap.in.yajurvedaproject.utils.PrefUtils;

import static kashyap.in.yajurvedaproject.common.ConstantsKt.IS_XMIN_ALARM_ALREADY_SET;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm Manger", "Triggerrrrd");
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            GeneralUtils.Companion.createAlarmManager(context.getApplicationContext(), 1, false);
            PrefUtils.saveToPrefs(context, IS_XMIN_ALARM_ALREADY_SET, true);
        }
        GeneralUtils.Companion.createNotification(
                context,
                "Remainder to upload your picture, body temperature, health problems, if any .",
                "This data will be sent to your health administrator.",
                R.drawable.splash
        );
        //get and send location information
    }
}
