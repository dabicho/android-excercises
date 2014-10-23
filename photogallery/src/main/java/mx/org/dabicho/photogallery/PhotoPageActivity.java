package mx.org.dabicho.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by dabicho on 10/23/14.
 */
public class PhotoPageActivity extends SingleSupportFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
