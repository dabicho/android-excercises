package mx.org.dabicho.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by dabicho on 15/10/14.
 */
public class PhotoGalleryActivity extends SingleSupportFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
