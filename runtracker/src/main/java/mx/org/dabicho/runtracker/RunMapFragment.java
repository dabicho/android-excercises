package mx.org.dabicho.runtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by dabicho on 28/10/14.
 */
public class RunMapFragment extends SupportMapFragment {
    private static final String ARG_RUN_ID="RUN_ID";

    private GoogleMap mGoogleMap;

    public static RunMapFragment newInstance(long runId){
        Bundle args=new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        mGoogleMap=getMap();
        mGoogleMap.setMyLocationEnabled(true);
        return v;
    }
}
