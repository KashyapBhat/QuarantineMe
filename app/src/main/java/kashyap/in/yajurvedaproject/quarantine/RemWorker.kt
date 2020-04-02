package kashyap.`in`.yajurvedaproject.quarantine

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kashyap.`in`.yajurvedaproject.R
import kashyap.`in`.yajurvedaproject.utils.GeneralUtils
import java.util.concurrent.TimeUnit

class RemWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        GeneralUtils.createNotification(
            context,
            "Remainder to upload your picture, body temperature, health problems, if any .",
            "This data will be sent to your health administrator.",
            R.drawable.splash
        )
        for (i in 1..10) {
            TimeUnit.SECONDS.sleep(1)
            Log.d("ExampleWorker", "progress: $i")
        }
        return Result.success()
    }
}
