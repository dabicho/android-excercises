package mx.org.dabicho.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;

/**
 * Created by dabicho on 10/21/14.
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";

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
        String lastResultId = lPreferences.getString(FlickrFetcher.PREF_LAST_RESULT_ID,null);
        ArrayList<GalleryItem> lItems;
        if (query != null) {
            lItems = new FlickrFetcher().search(query).getItems();
        } else {
            lItems = new FlickrFetcher().fetchItems();

        }
        if (lItems.size() == 0)
            return;
        String resultId=lItems.get(0).getId();
        if(!resultId.equals(lastResultId))
            Log.i(TAG,"Got a new result: "+resultId);
        else {
            Log.i(TAG, "Got an old result: "+resultId);
        }

        lPreferences.edit().putString(FlickrFetcher.PREF_LAST_RESULT_ID,resultId)
                .commit();

        Log.i(TAG, "Received an intent: " + intent);
    }
}
