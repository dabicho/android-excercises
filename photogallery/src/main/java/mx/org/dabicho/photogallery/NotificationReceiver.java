package mx.org.dabicho.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.util.Log.i;

/**
 * Created by dabicho on 10/23/14.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        i(TAG, "onReceive: received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK) {
            // Alguna actividad que recibió el broadcast canceló el broadcast
            return;
        } // Si nadie canceló el broadcast, se postea la notificación
        int requestCode = intent.getIntExtra("REQUEST_CODE", 0);
        Notification lNotification = (Notification) intent.getParcelableExtra("NOTIFICATION");

        NotificationManager lNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.notify(requestCode, lNotification);
    }
}
