package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/*
Para soporte con versiones anteriores de android, en lugar de extender Activity debe extender
FragmentActivity
 */
public class CriminalIntentActivity extends SingleFragmentActivity implements CrimeFragment.OnFragmentInteractionListener {
    private static final String TAG = "CriminalIntentActivity";

    @Override
    protected Fragment createFragment() {
        Log.d(TAG,"createFragment");
        return new CrimeFragment();
    }



    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: " + uri);
    }
}
