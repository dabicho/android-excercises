package mx.org.dabicho.remotecontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

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

    /**
     * Método para ser sobreescrito por clases herederas para devolver el id del resource que va
     * a ser expuesto en la interfaz
     * @return
     */
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());


        FragmentManager fm = getSupportFragmentManager();

        Fragment lFragment=fm.findFragmentById(R.id.fragmentContainer);

        if(lFragment==null){
            lFragment=createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,lFragment).commit();
        }


    }
}
