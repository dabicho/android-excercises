package mx.org.dabicho.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import static android.util.Log.e;
import static android.util.Log.i;


/**
 * Receptor de intent de inicio
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        i(TAG, "onReceive: " + intent.getAction());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isOn = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlarm(context, isOn);
    }
}
