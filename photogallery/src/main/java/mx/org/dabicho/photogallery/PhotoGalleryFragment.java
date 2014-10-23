package mx.org.dabicho.photogallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;
import mx.org.dabicho.photogallery.services.BitmapCacheManager;

/**
 * Fragmento principal de la galer+ía
 */
public class PhotoGalleryFragment extends VisibleSupportFragment {
    private static final String TAG = "PhotoGalleryFragment";
    private static final int MINIMUM_REMAINING_IMAGES = 30;

    private ThumbnailDownloader<ImageView> mViewThumbnailDownloader;



    GridView mGridView;
    /**
     * La lista de elementos (imágenes) a mostrar
     */
    ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mViewThumbnailDownloader = new ThumbnailDownloader<ImageView>(new Handler());
        mViewThumbnailDownloader.setListener(new ThumbnailDownloader.Listener<ImageView>() {

            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible())
                    imageView.setImageBitmap(thumbnail);
            }
        });
        mViewThumbnailDownloader.start();
        mViewThumbnailDownloader.getLooper();

        Log.i(TAG, "ThumbnailDownloader thread started");
        clearItemsList();
        updateItems();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);

        setUpAdapter();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem lItem = mItems.get(position);
                Uri photoPageUri=Uri.parse(lItem.getPhotoPageUrl());

                Intent i = new Intent(Intent.ACTION_VIEW, photoPageUri);

                startActivity(i);

            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        mViewThumbnailDownloader.clearQueue();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mViewThumbnailDownloader.quit();
        Log.i(TAG, "Background thumbnailDownloader thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            String previousSearch = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetcher.PREF_SEARCH_QUERY, "");
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            final SearchView lSearchView = (SearchView) searchItem.getActionView();

            SearchManager lSearchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo lSearchableInfo = lSearchManager.getSearchableInfo(name);
            lSearchView.setSearchableInfo(lSearchableInfo);
            lSearchView.setSubmitButtonEnabled(true);
            lSearchView.setIconified(true);
            lSearchView.setQuery(previousSearch, false);


            lSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    String previousSearch = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetcher.PREF_SEARCH_QUERY, "");
                    lSearchView.setQuery(previousSearch, false);
                    return false;
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:

                clearItemsList();
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                clearItemsList();
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetcher.PREF_SEARCH_QUERY, null)
                        .commit();

                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);

        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
            toggleItem.setIcon(android.R.drawable.button_onoff_indicator_on);
        } else {
            toggleItem.setTitle(R.string.start_polling);
            toggleItem.setIcon(android.R.drawable.button_onoff_indicator_off);
        }
    }

    public void clearItemsList() {
        if (mItems != null)
            mItems.clear();
        FlickrFetcher.resetPageCount();
    }

    /**
     * Tarea asíncrona para descargar los datos de imágenes de la galería
     */
    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
        private String toastMessage = null;

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            Activity lActivity = getActivity();

            if (lActivity == null)
                return new ArrayList<GalleryItem>();
            String query = PreferenceManager.getDefaultSharedPreferences(lActivity)
                    .getString(FlickrFetcher.PREF_SEARCH_QUERY, null);

            if (query != null) {
                FlickrResult lResult = new FlickrFetcher().search(query);
                toastMessage = lResult.getItemsFound() + " images found for \"" + query + "\".";
                return lResult.getItems();
            } else {
                return new FlickrFetcher().fetchItems();
            }

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {


            if (mItems == null || mItems.isEmpty()) {

                SharedPreferences lPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                lPreferences.edit().putString(FlickrFetcher.PREF_LAST_RESULT_ID, galleryItems.get(0).getId()).apply();


                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                //if(mItems==null)
                mItems = galleryItems;
                //else
                //    mItems.addAll(galleryItems);
                setUpAdapter();
                Log.i(TAG,"Elementos totales: "+mItems.size());
            } else {
                mItems.addAll(galleryItems);
                Log.i(TAG,"Elementos totales: "+mItems.size());
                ((ArrayAdapter) mGridView.getAdapter()).notifyDataSetChanged();
            }

        }
    }


    void updateItems() {
        new FetchItemsTask().execute();
    }

    /**
     * Configura los datos que respaldan al gridview
     */
    void setUpAdapter() {

        if (getActivity() == null || mGridView == null) {
            return;
        }

        if (mItems != null) {

            mGridView.setAdapter(new GalleryItemAdapter(mItems));

        } else {

            mGridView.setAdapter(null);
        }


    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d(TAG, "Asking for view " + position);

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            if (MINIMUM_REMAINING_IMAGES == mItems.size() - position)
                updateItems();
            GalleryItem lItem = getItem(position);
            if (lItem.getUrl() == null)
                return convertView;
            if (BitmapCacheManager.getInstance().get(lItem.getUrl()) == null) {
                imageView.setImageResource(R.drawable.brian_up_close);
                mViewThumbnailDownloader.queueThumbnail(imageView, lItem.getUrl());
            } else {
                mViewThumbnailDownloader.dequeueThumbnail(imageView);
                imageView.setImageBitmap(BitmapCacheManager.getInstance().get(lItem.getUrl()));
            }
            int limit = getCount() >= position + 19 ? position + 19 : getCount() - 1;
            for (int i = position + 1; i < limit; i++) {


                if (getItem(i).getUrl() == null)
                    continue;

                mViewThumbnailDownloader.queuePreloadCache(getItem(i).getUrl());
            }
            return convertView;
        }
    }

}
