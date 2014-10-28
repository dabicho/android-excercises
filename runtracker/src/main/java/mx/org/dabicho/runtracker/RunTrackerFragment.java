package mx.org.dabicho.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import mx.org.dabicho.runtracker.model.Run;

/**
 * Fragmento que define la lista de datos de GPS
 */
public class RunTrackerFragment extends Fragment {
    private static final String TAG = "RunTrackerFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_RUN=0;
    private static final int LOAD_LOCATION=1;

    private Button mStartButton, mStopButton;
    private TextView mStartedTextView, mLatitudeTextView,
            mLongitudeTextView, mAltitudeTextView,
            mDurationTextView;
    private Location mLastLocation;

    private RunManager mRunManager;
    private Run mRun;

    public static RunTrackerFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunTrackerFragment rf = new RunTrackerFragment();
        rf.setArguments(args);
        return rf;
    }

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if (!mRunManager.isTrackingRun(mRun))
                return;
            mLastLocation = loc;
            if (isVisible())
                updateUI();
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.getInstance(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager lm=getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_tracker
                , container, false);

        mStartedTextView = (TextView) view.findViewById(R.id.run_startedTextView);
        mLatitudeTextView = (TextView) view.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView) view.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView) view.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView) view.findViewById(R.id.run_durationTextView);

        mStartButton = (Button) view.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRun == null) {
                    mRun = mRunManager.startNewRun();
                } else {
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });
        mStopButton = (Button) view.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                updateUI();
            }
        });
        updateUI();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {

        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    public void updateUI() {
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
        boolean started = mRunManager.isTrackingRun();

        if (mRun != null)
            mStartedTextView.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mRun != null && mLastLocation != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
        }
        mDurationTextView.setText(mRun.formatDuration(durationSeconds));
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingThisRun);
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run>{
        @Override
        public Loader<Run> onCreateLoader(int i, Bundle bundle) {
            return new RunLoader(getActivity(), bundle.getLong(ARG_RUN_ID));

        }

        @Override
        public void onLoadFinished(Loader<Run> runLoader, Run run) {
            mRun=run;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> runLoader) {

        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location> {
        @Override
        public Loader<Location> onCreateLoader(int i, Bundle bundle) {
            return new LastLocationLoader(getActivity(), bundle.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> locationLoader, Location location) {
            mLastLocation=location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> locationLoader) {

        }
    }

}
