package mx.org.dabicho.runtracker;

import android.support.v4.app.Fragment;

/**
 * Actividad para presentar la lista de corridas
 */
public class RunListActivity extends SingleSupportFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
