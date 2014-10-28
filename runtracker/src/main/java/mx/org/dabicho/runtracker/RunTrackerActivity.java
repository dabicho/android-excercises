package mx.org.dabicho.runtracker;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RunTrackerActivity extends SingleSupportFragmentActivity {
    public static final String EXTRA_RUN_ID = "mx.org.dabicho.runtracker.run_id";


    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return RunTrackerFragment.newInstance(runId);
        }
        return new RunTrackerFragment();
    }
}
