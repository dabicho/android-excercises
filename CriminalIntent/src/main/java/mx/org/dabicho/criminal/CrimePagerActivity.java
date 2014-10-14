package mx.org.dabicho.criminal;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import mx.org.dabicho.criminal.model.Crime;
import mx.org.dabicho.criminal.model.CrimeLab;

/**
 * Created by dabicho on 10/2/14.
 */
/*
Utilizamos FragmentActivity de la librería de support por que ViewPager lo requiere
 */
public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.OnFragmentInteractionListener, CrimeFragment.Callbacks{
    private final static String TAG="CrimePagerActivity";
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager=new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes= CrimeLab.getInstance(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                Crime lCrime=mCrimes.get(i);
                return CrimeFragment.newInstance(lCrime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        // Define cuantas páginas son cargadas hacia adelante y atrás previendo peticiones futuras
        // Default es 1
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Crime lCrime=mCrimes.get(i);

                if(lCrime.getTitle()!=null) {

                    setTitle(getString(R.string.crime_title_part)+lCrime.getTitle());
                }
                else
                    setTitle(getString(R.string.crime_title_part));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        // Buscar el crimen con el id dado en el extra
        UUID crimeId=(UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for(int i=0; i<mCrimes.size();i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {
        // No hace nada actualmente
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        // No se hace nada pues el pager se carga únicamente en teléfonos
    }
}
