package mx.org.dabicho.remotecontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Clase que extiende de Activity y que inicializa para contener un fragmento
 */
abstract public class SingleFragmentActivity extends Activity{
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

        FragmentManager fm = getFragmentManager();

        Fragment lFragment=fm.findFragmentById(R.id.fragmentContainer);

        if(lFragment==null){
            lFragment=createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,lFragment).commit();
        }


    }
}
