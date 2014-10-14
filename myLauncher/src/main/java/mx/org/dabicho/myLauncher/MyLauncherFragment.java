package mx.org.dabicho.myLauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragmento de el Launcher
 */
public class MyLauncherFragment extends ListFragment {
    private static final String TAG="MyLauncherFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creamos un Intent para investigar las actividades instaladas que se pueden
        // ejecutar desde un launcher

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager pm=getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent,0);
        Log.i(TAG,"He encontrado "+activities.size()+ "actividades.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.loadLabel(pm).toString(),rhs.loadLabel(pm).toString());

            }
        });
        ArrayAdapter<ResolveInfo> lAdapter=new ArrayAdapter<ResolveInfo>(getActivity(),android.R.layout.simple_list_item_1,activities){
            @Override
           public View getView(int pos, View convertView, ViewGroup parent) {
               PackageManager lPackageManager=getActivity().getPackageManager();
               View v = super.getView(pos, convertView, parent);
                TextView lTextView=(TextView)v;
                ResolveInfo ri=getItem(pos);
                lTextView.setText(ri.loadLabel(pm));
                return v;

           }
        };

        setListAdapter(lAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ResolveInfo lResolveInfo=(ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo lActivityInfo=lResolveInfo.activityInfo;
        if(getActivity()==null) return;
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClassName(lActivityInfo.applicationInfo.packageName,lActivityInfo.name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
