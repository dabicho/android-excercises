package mx.org.dabicho.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by dabicho on 15/10/14.
 */
public class PhotoGalleryActivity extends SingleSupportFragmentActivity {
    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment lFragment = (PhotoGalleryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Receiverd a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(FlickrFetcher.PREF_SEARCH_QUERY, query)
                    .commit();


        }
        lFragment.clearItemsList();

        lFragment.updateItems();

    }

    @Override
    public boolean onSearchRequested() {
        Log.d(TAG, "SEARCH REQUESTED");
        String previousSearch = PreferenceManager.getDefaultSharedPreferences(this).getString(FlickrFetcher.PREF_SEARCH_QUERY, "Enter your search");
        startSearch(previousSearch, true, null, false);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableHttpCaching();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCaching();
    }

    private void stopCaching() {

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }

    }

    private void enableHttpCaching() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                File httpCacheDir = getApplicationContext().getExternalCacheDir();
                if (httpCacheDir == null)
                    httpCacheDir = getApplicationContext().getCacheDir()
                            ;
                long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } catch (IOException e) {
                Log.i(TAG
                        , "OVER ICS: HTTP response cache failed:" + e);
            }
        }
        /*
        else
        {
            File httpCacheDir = new File(getApplicationContext().getCacheDir()
                    , "http");
            try {
                com.integralblue.httpresponsecache.HttpResponseCache.install
                        (httpCacheDir, 10 * 1024 * 1024);
            } catch (IOException e) {
                Log.i(Constants.TAG_REPONSE_CACHING_FAILED
                        , "UNDER ICS : HTTP response cache  failed:" + e);
            }
        }
        */
    }
}
