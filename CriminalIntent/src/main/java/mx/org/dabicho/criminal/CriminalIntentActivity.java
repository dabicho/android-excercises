package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/*
Para soporte con versiones anteriores de android, en lugar de extender Activity debe extender
FragmentActivity
 */
public class CriminalIntentActivity extends Activity implements CrimeFragment.OnFragmentInteractionListener {
    private static final String TAG = "CriminalIntentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criminal_intent);


        /*
        Se crea el fragmento y se agrega. Esto ocasiona que se ejecuten onAttach, onCreate y
        onCreateView del fragmento
         */
        FragmentManager fm = getFragmentManager();

        Fragment lFragment = fm.findFragmentById(R.id.fragmentContainer);

        if (lFragment == null) {
            lFragment = new CrimeFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, lFragment).commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: " + uri);
    }
}
