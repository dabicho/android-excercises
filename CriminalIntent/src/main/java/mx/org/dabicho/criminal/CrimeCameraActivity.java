package mx.org.dabicho.criminal;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by dabicho on 10/7/14.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Deben ejecutarse antes de setContentView, que se ejecuta en onCreate de SingleFragmentActivity,
        // por lo que es necesario definir onCreate aquí y no se puede definir en el fragmento
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

    }


}
