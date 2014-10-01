package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by dabicho on 10/1/14.
 */
abstract public class SingleFragmentActivity extends Activity{
    private static final String TAG="SingleFragmentActivity";
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getFragmentManager();

        Fragment lFragment=fm.findFragmentById(R.id.fragmentContainer);

        if(lFragment==null){
            lFragment=createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,lFragment).commit();
        }


    }
}
