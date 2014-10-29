package mx.org.dabicho.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by dabicho on 28/10/14.
 */
public class RunMapActivity extends SingleSupportFragmentActivity {
    public static final String EXTRA_RUN_ID="mx.org.dabicho.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId=getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if(runId!=-1){
            return RunMapFragment.newInstance(runId);
        } else
            return new RunMapFragment();
    }
}
