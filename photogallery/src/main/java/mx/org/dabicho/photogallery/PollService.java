package mx.org.dabicho.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;

/**
 * Servicio de intent para verificar existencia de nuevos resultados
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60 * 15; // Cada 15 minutos
    public static final String PERM_PRIVATE="mx.org.dabicho.photogallery.PRIVATE";

    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String ACTION_SHOW_NOTIFICATION = "mx.org.dabicho.photogallery.SHOW_NOTIFICATION";

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Llamadas depreciadas para conservar compatibilidad
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
        if (!isNetworkAvailable)
            return;

        SharedPreferences lPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String query = lPreferences.getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
        String lastResultId = lPreferences.getString(FlickrFetcher.PREF_LAST_RESULT_ID, null);
        ArrayList<GalleryItem> lItems;
        if (query != null) {
            lItems = new FlickrFetcher().search(query, 1).getItems();
        } else {
            lItems = new FlickrFetcher().fetchItems(1);

        }
        if (lItems.size() == 0) {
            lPreferences.edit().putString(FlickrFetcher.PREF_LAST_RESULT_ID, "")
                    .apply();
            return;
        }
        String resultId = lItems.get(0).getId();
        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);
            Resources r = getResources();
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
                    PhotoGalleryActivity.class), 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(r.getString(R.string.new_pictures_text))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentText(r.getString(R.string.new_pictures_title))
                    .setContentText(r.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true).build();

            showBackgroundNotification(0,notification);
        } else {
            Log.i(TAG, "Got an old result: " + resultId);
        }

        lPreferences.edit().putString(FlickrFetcher.PREF_LAST_RESULT_ID, resultId)
                .apply();

        Log.i(TAG, "Received an intent: " + intent);
    }

    /**
     * Configura una alarma para lanzar un intent a PollService cada cantidad
     * fija de segundos
     * @param context
     * @param isOn
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, PollService.class);

        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    void showBackgroundNotification(int requestCode, Notification notification){
        Intent lIntent=new Intent(ACTION_SHOW_NOTIFICATION);
        lIntent.putExtra("REQUEST_CODE", requestCode);
        lIntent.putExtra("NOTIFICATION", notification);

        sendOrderedBroadcast(lIntent,PERM_PRIVATE, null,null, Activity.RESULT_OK, null, null);
    }
}
