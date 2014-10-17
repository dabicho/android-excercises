package mx.org.dabicho.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private int lastPageSize = 0;

    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);

        setUpAdapter();

        return v;
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
            ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brian_up_close);

            return convertView;
        }
    }

}
