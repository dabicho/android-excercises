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

import java.io.IOException;
import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;

/**
 * Fragmento principal de la galer+ía
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
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
            if(mItems==null ) {
                mItems = galleryItems;
                setUpAdapter();
            }else {
                mItems.addAll(galleryItems);
                
            }

        }
    }

    void setUpAdapter() {
        Log.d(TAG,"setUpAdapter");
        if (getActivity() == null || mGridView == null) {
            return;
        }
        Log.d(TAG,"setUpAdapter Activity and GridView ready");
        if (mItems != null) {
            Log.d(TAG,"setUpAdapter Items ready");
            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(), android.R.layout.simple_gallery_item, mItems){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if(mItems.size()-position==40){
                        // Iniciar otra tarea para tomar más y agregarlos a la lista
                        new FetchItemsTask().execute();
                    }
                    return super.getView(position, convertView, parent);
                }
            });
        } else {
            Log.d(TAG,"setUpAdapter No Items");
            mGridView.setAdapter(null);
        }


    }
}
