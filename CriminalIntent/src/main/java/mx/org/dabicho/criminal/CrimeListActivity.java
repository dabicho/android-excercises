package mx.org.dabicho.criminal;

import android.app.Fragment;
import android.util.Log;

/**
 * Created by dabicho on 10/1/14.
 */
public class CrimeListActivity extends SingleFragmentActivity {

private static final String TAG="SingleFragmentActivity";

    @Override
    protected Fragment createFragment() {
        
        return new CrimeListFragment();
    }
}
