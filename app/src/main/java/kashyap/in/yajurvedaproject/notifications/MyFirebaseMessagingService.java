package kashyap.in.yajurvedaproject.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kashyap.in.yajurvedaproject.R;
import kashyap.in.yajurvedaproject.utils.GeneralUtils;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String notificationText = "";
//        if (!remoteMessage.getData().isEmpty() && remoteMessage.getData().containsKey(ConstantsKt.LINK)) {
//            redirectionUrl = remoteMessage.getData().get(Constants.LINK);
//        }
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null) {
            notificationText = remoteMessage.getNotification().getBody();
        }
        GeneralUtils.Companion.createNotification(getApplicationContext(), getString(R.string.app_name), notificationText, R.drawable.splash);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
