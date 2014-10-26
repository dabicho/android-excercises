package mx.org.dabicho.runtracker;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RunTrackerActivity extends SingleSupportFragmentActivity {



    @Override
    protected Fragment createFragment() {
        return new RunTrackerFragment();
    }
}
