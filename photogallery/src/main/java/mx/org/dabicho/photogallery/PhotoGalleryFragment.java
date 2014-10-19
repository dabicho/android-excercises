package mx.org.dabicho.photogallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;

/**
 * Fragmento principal de la galer+ía
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private static final int MINIMUM_REMAINING_IMAGES=30;

    private ThumbnailDownloader<ImageView> mViewThumbnailDownloader;
    private int lastPageSize = 0;


    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);

        setUpAdapter();

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

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {

            return new FlickrFetcher().fetchItems();

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            lastPageSize = galleryItems.size();
            if (mItems == null) {
                mItems = galleryItems;
                setUpAdapter();
            } else {
                mItems.addAll(galleryItems);
                ((ArrayAdapter) mGridView.getAdapter()).notifyDataSetChanged();
            }

        }
    }

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
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            if(MINIMUM_REMAINING_IMAGES==mItems.size()-position)
                new FetchItemsTask().execute();
            GalleryItem lItem = getItem(position);
            if(lItem.getUrl()==null)
                return convertView;
            if (BitmapCacheManager.getInstance().get(lItem.getUrl()) == null) {
                imageView.setImageResource(R.drawable.brian_up_close);
                mViewThumbnailDownloader.queueThumbnail(imageView, lItem.getUrl());
            } else {
                imageView.setImageBitmap(BitmapCacheManager.getInstance().get(lItem.getUrl()));
            }
            int limit= getCount()>=position+19?position+19:getCount()-1;
            for(int i=position+1; i<limit; i++) {


                if(getItem(i).getUrl()==null)
                    continue;

                mViewThumbnailDownloader.queuePreloadCache(getItem(i).getUrl());
            }
            return convertView;
        }
    }

}
