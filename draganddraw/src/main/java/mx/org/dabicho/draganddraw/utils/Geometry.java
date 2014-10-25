package mx.org.dabicho.draganddraw.utils;

import android.graphics.PointF;
import android.util.Log;

import static android.util.Log.i;

/**
 * Created by dabicho on 10/24/14.
 */
public class Geometry {
    private static final String TAG = "Geometry";
    public static double angleBetween(PointF v, PointF p1, PointF p2) {
        i(TAG, "angleBetween: "+p1+" "+v+" "+p2);
        double d1v_2=Math.pow(p1.x-v.x,2)+Math.pow(p1.y-v.y,2);
        double d2v_2=Math.pow(p2.x-v.x,2)+Math.pow(p2.y-v.y,2);
        double d12_2=Math.pow(p1.x-p2.x,2)+Math.pow(p1.y-p2.y,2);
        double angle = -Math.toDegrees(Math.acos((d1v_2 + d2v_2 - d12_2)/(2*Math.sqrt(d1v_2)*Math.sqrt(d2v_2))))%360;
        if(angle == Double.NaN)
            angle=0;
        return angle;
    }
}
