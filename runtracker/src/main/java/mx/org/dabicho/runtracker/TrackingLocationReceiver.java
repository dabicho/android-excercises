package mx.org.dabicho.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import static android.util.Log.i;

/**
 * Receptor de mensajes broadcast para guardarlos en la bd
 */
public class TrackingLocationReceiver extends LocationReceiver{
    private static final String TAG = "TrackingLocationReceiver";
    @Override
    protected void onLocationReceived(Context context, Location loc) {
        super.onLocationReceived(context, loc);
        RunManager.getInstance(context).insertLocation(loc);
        i(TAG, "onLocationReceived: localidad insertada");
    }
}
