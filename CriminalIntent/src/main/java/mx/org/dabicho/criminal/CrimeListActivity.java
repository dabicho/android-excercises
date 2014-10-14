package mx.org.dabicho.criminal;




import android.content.Intent;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import mx.org.dabicho.criminal.model.Crime;

/**
 * Administra la lista de crímenes. Maneja dos frames si es una tableta
 */
public class CrimeListActivity extends SingleSupportFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks, CrimeFragment.OnFragmentInteractionListener {

private static final String TAG="CrimeListActivity";

    @Override
    protected Fragment createFragment() {

        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detailFragmentContainer)==null) {
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(i);
        } else {
            Log.d(TAG,"CrimeSelected");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            Fragment oldDetail=fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail=CrimeFragment.newInstance(crime.getId());
            if(oldDetail!=null){
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer,newDetail);
            ft.commit();
            Log.d(TAG,"CrimeSelected 2");
        }
    }


    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm=getSupportFragmentManager();
        CrimeListFragment lListFragment=(CrimeListFragment)fm.findFragmentById(R.id.fragmentContainer);
        lListFragment.updateUI();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // NADA
    }
}
