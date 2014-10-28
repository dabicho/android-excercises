package mx.org.dabicho.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Cargador de localización de BD
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context, long runId){
        super(context);
        mRunId=runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.getInstance(getContext()).getLastLocationForRun(mRunId);
    }
}
