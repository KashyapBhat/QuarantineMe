package kashyap.in.yajurvedaproject.worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import kashyap.in.yajurvedaproject.R;
import kashyap.in.yajurvedaproject.utils.GeneralUtils;

public class AlarmWorker extends Worker {
    public static final String TAG = AlarmWorker.class.getSimpleName();

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork AlarmWorker");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> GeneralUtils.Companion.createNotification(getApplicationContext(), "Hey", "There", R.drawable.ic_veda), 100);
        return Result.success();
    }

    public static void startLogoutWorker(Context context, int alarmHour, int alarmMin, String uniqueWorkerName) {
        stopLogoutWorker(context, uniqueWorkerName);
        Log.e(TAG, "StartWork AlarmWorker");
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType
                .NOT_REQUIRED).build();
        long timeDiff = getTimeDiff(alarmHour, alarmMin, false);
        if (timeDiff > 0) {
            OneTimeWorkRequest locationWork = new OneTimeWorkRequest.Builder(
                    AlarmWorker.class).setConstraints(constraints)
                    .addTag(AlarmWorker.TAG)
                    .setInitialDelay(timeDiff, TimeUnit.SECONDS).build();
            WorkManager.getInstance(context).beginUniqueWork(uniqueWorkerName, ExistingWorkPolicy.REPLACE, locationWork).enqueue();
        }
    }

    public static void stopLogoutWorker(Context context, String uniqueWorkerName) {
        Log.e("Logout", "Logout stop");
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkerName);
    }

    private static long getTimeDiff(int Hours, int Min, boolean login) {
        long seconds;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY, Hours);
        c2.set(Calendar.MINUTE, Min);
        c2.set(Calendar.SECOND, 0);
        if (login) {
            c2.add(Calendar.DAY_OF_YEAR, 1);
        }
        seconds = (c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000;
        return seconds;
    }
}