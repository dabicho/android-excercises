package mx.org.dabicho.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by dabicho on 25/10/14.
 */
public class RunManager {
    private static final String TAG =  "RunManager";

    public static final String ACTION_LOCATION="mx.org.dabicho.runtracker.ACTION_LOCATION";

    private static RunManager sRunManager;

    private Context mAppContext;
    private LocationManager mLocationManager;

    public RunManager(Context appContext) {
        mAppContext=appContext;
        mLocationManager=(LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);

    }

    public static RunManager getInstance(Context c) {
        if(sRunManager==null) {
            sRunManager=new RunManager(c.getApplicationContext());
        }

        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate){
        Intent broadcast=new Intent(ACTION_LOCATION);
        int flags = shouldCreate?0:PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider =LocationManager.GPS_PROVIDER;

        Location lastKnown=mLocationManager.getLastKnownLocation(provider);
        if(lastKnown!=null) {
            //lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);

        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }


    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if(pi!=null){
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun(){
        return getLocationPendingIntent(false)!=null;
    }
}
