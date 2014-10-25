package mx.org.dabicho.draganddraw.model;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.io.Serializable;

import static android.util.Log.i;

/**
 * Una caja
 */
public class Box implements Serializable {
    private static final String TAG = "Box";
    private static final long serialVersionUID = 1L;
    private PointF mOrigin;
    private PointF mCurrent;
    private Double angle=0D;

    public Box(PointF origin) {
        mOrigin = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Path getAsPath() {
        float left= Math.min(mOrigin.x, mCurrent.x);
        float top = Math.min(mOrigin.y, mCurrent.y);
        float right = Math.max(mOrigin.x, mCurrent.x);
        float bottom = Math.max(mOrigin.y, mCurrent.y);


        Matrix lMatrix=new Matrix();
        lMatrix.setRotate(angle.floatValue(), right, top);
        float[] points= {left,top,right,top,right,bottom,left,bottom};

        lMatrix.mapPoints(points);
        Path lPath=new Path();

        lPath.moveTo(points[0], points[1]);
        lPath.lineTo(points[2],points[3]);
        lPath.lineTo(points[4],points[5]);
        lPath.lineTo(points[6],points[7]);
        lPath.lineTo(points[0],points[1]);

        return lPath;

    }
}
