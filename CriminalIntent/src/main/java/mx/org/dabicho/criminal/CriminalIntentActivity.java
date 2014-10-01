package mx.org.dabicho.criminal;


import android.app.Fragment;

import android.net.Uri;

import android.util.Log;

import java.util.UUID;

/*
Para soporte con versiones anteriores de android, en lugar de extender Activity debe extender
FragmentActivity
 */
public class CriminalIntentActivity extends SingleFragmentActivity implements CrimeFragment.OnFragmentInteractionListener {
    private static final String TAG = "CriminalIntentActivity";

    @Override
    protected Fragment createFragment() {
        Log.d(TAG,"createFragment");
        UUID lCrimeId=(UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(lCrimeId);

    }



    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: " + uri);
    }
}
