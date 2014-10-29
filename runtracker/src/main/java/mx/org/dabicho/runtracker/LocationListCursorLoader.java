package mx.org.dabicho.runtracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by dabicho on 10/29/14.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;

    public LocationListCursorLoader(Context c, long runId){
        super(c);
        mRunId=runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.getInstance(getContext()).queryLocationsForRun(mRunId);
    }
}
