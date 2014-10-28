package mx.org.dabicho.runtracker;

import android.content.Context;

import mx.org.dabicho.runtracker.model.Run;

/**
 * Created by dabicho on 10/28/14.
 */
public class RunLoader extends DataLoader<Run> {
    private long mRunId;

    public RunLoader(Context context, long runId){
        super(context);
        mRunId=runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.getInstance(getContext()).getRun(mRunId);
    }
}
