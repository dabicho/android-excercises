package mx.org.dabicho.runtracker;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import mx.org.dabicho.runtracker.model.Run;

import static android.util.Log.e;

/**
 * Created by dabicho on 25/10/14.
 */
public class RunManager {
    private static final String TAG = "RunManager";

    private static final int NOTIFICACION_RASTREO_ID=0;

    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.current";

    public static final String ACTION_LOCATION = "mx.org.dabicho.runtracker.ACTION_LOCATION";


    private static RunManager sRunManager;

    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPreferences;
    private long mCurrentRunId;

    public RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(mAppContext);
        mPreferences = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPreferences.getLong(PREF_CURRENT_RUN_ID, -1);

    }

    public static RunManager getInstance(Context c) {
        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }

        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            //lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);

        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

        pi = PendingIntent.getActivity(mAppContext, 0, new Intent(mAppContext,
                RunTrackerActivity.class), 0);
        // TODO Construir el intent para RunTrackerActivity correctamente
        Notification notification= new NotificationCompat.Builder(mAppContext)
                .setTicker(mAppContext.getString(R.string.tracking_message))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentText(mAppContext.getString(R.string.tracking_message))
                .setContentIntent(pi).setAutoCancel(false).build();

        NotificationManager lNotificationManager = (NotificationManager)mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.notify(NOTIFICACION_RASTREO_ID, notification);


    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public Run startNewRun(){
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run){
        mCurrentRunId=run.getId();
        mPreferences.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
        startLocationUpdates();
    }

    public void stopRun(){
        stopLocationUpdates();
        mCurrentRunId=-1;
        mPreferences.edit().remove(PREF_CURRENT_RUN_ID).commit();
        NotificationManager lNotificationManager = (NotificationManager)mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.cancel(NOTIFICACION_RASTREO_ID);
    }

    private Run insertRun(){
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns(){
        return mHelper.queryRuns();
    }

    public void insertLocation(Location location){
        if(mCurrentRunId!=-1){
            mHelper.insertLocation(mCurrentRunId, location);
        } else {
            e(TAG, "insertLocation: location received with no tracking run: ignoring.");
        }
    }


    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public Run getRun(long id) {
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        if(!cursor.isAfterLast())
            run=cursor.getRun();
        cursor.close();
        return run;
    }

    public boolean isTrackingRun(Run run){
        return run!=null && run.getId()==mCurrentRunId;
    }

    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDatabaseHelper.LocationCursor cursor=mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            location=cursor.getLocation();
        }
        cursor.close();
        return location;
    }
}
