package mx.org.dabicho.runtracker;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static android.util.Log.i;


public class RunTrackerActivity extends SingleSupportFragmentActivity {
    private static final String TAG = "RunTrackerActivity";
    public static final String EXTRA_RUN_ID = "mx.org.dabicho.runtracker.run_id";


    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        i(TAG, "createFragment: ");
        if (runId != -1) {
            i(TAG, "createFragment: abriendo "+runId);
            return RunTrackerFragment.newInstance(runId);
        }
        return new RunTrackerFragment();
    }
}
