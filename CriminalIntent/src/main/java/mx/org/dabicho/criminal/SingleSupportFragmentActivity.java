package mx.org.dabicho.criminal;

import android.app.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * Clase que extiende de Activity y que inicializa para contener un fragmento
 */
abstract public class SingleSupportFragmentActivity extends FragmentActivity{
    private static final String TAG="SingleFragmentActivity";

    /**
     *
     * @return El fragmento que hospeda esta actividad
     */
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);


        FragmentManager fm = getSupportFragmentManager();

        Fragment lFragment=fm.findFragmentById(R.id.fragmentContainer);

        if(lFragment==null){
            lFragment=createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,lFragment).commit();
        }


    }
}
