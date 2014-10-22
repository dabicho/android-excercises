package mx.org.dabicho.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by dabicho on 15/10/14.
 */
public class PhotoGalleryActivity extends SingleSupportFragmentActivity {
    private static final String TAG="PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment lFragment = (PhotoGalleryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query=intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Receiverd a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(FlickrFetcher.PREF_SEARCH_QUERY,query)
                    .commit();



        }
        lFragment.clearItemsList();

        lFragment.updateItems();

    }

    @Override
    public boolean onSearchRequested() {
        Log.d(TAG,"SEARCH REQUESTED");
        String previousSearch=PreferenceManager.getDefaultSharedPreferences(this).getString(FlickrFetcher.PREF_SEARCH_QUERY,"Enter your search");
        startSearch(previousSearch,true,null,false);
        return true;
    }


}
