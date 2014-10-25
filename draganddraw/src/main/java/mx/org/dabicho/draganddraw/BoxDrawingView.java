package mx.org.dabicho.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import mx.org.dabicho.draganddraw.model.Box;
import mx.org.dabicho.draganddraw.utils.Geometry;

import static android.util.Log.i;

/**
 * Una vista para representar el área donde se dibujan cajas
 */
public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<Box>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private PointF mRotationPoint;
    private Integer mRotationId = -1;
    private Integer mMainId = -1;



    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);// Alfa 22 y rojo
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0); // opaco, alfa 255
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parentState = super.onSaveInstanceState();
        i(TAG, "onSaveInstanceState: ");
        Bundle viewState = new Bundle();
        viewState.putSerializable("boxes", mBoxes);
        viewState.putSerializable("currentBox", mCurrentBox);
        viewState.putParcelable("parentState", parentState);

        return viewState;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        i(TAG, "onRestoreInstanceState: ");

        Bundle lState = (Bundle) state;
        super.onRestoreInstanceState(((Bundle) state).getParcelable("parentState"));
        mBoxes = (ArrayList<Box>) lState.get("boxes");
        mCurrentBox = (Box) lState.get("box");
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF curr ;

        //i(TAG, "onTouchEvent: evento en x=" + curr.x + ", y=" + curr.y);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                curr = new PointF(event.getX(), event.getY());
                mCurrentBox = new Box(curr);
                mMainId = event.getPointerId(0);
                mRotationId = null;
                i(TAG, "onTouchEvent: ACTION_DOWN");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                if (mRotationId==null) {
                    int idx = event.getActionIndex();
                    mRotationId = event.getPointerId(idx);
                    curr = new PointF(event.getX(idx), event.getY(idx));
                    mRotationPoint = curr;
                }
                i(TAG, "onTouchEvent: ACTION_POINTER_DOWN "+mRotationId);
                break;
            case MotionEvent.ACTION_MOVE:

                if (mCurrentBox != null) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        if (event.getPointerId(i) == mMainId) {
                            i(TAG, "onTouchEvent: ACTION_MOVE MOVE");
                            curr = new PointF(event.getX(i), event.getY(i));
                            mCurrentBox.setCurrent(curr);
                            invalidate();
                        } else if (mRotationId!=null && event.getPointerId(i) == mRotationId) {
                            curr = new PointF(event.getX(i), event.getY(i));
                            mCurrentBox.setAngle((Geometry.angleBetween(
                                    mCurrentBox.getCurrent(),mRotationPoint,curr)
                                    + mCurrentBox.getAngle())%360);
                            mRotationPoint = curr;
                            i(TAG, "onTouchEvent: ACTION_MOVE ROTATE: "+mCurrentBox.getAngle());
                            invalidate();
                        }
                    }

                }

                break;
            case MotionEvent.ACTION_UP:

                if (mCurrentBox != null) {
                    curr = new PointF(event.getX(), event.getY());
                    mCurrentBox.setCurrent(curr);
                    mBoxes.add(mCurrentBox);
                    mCurrentBox = null;
                    mMainId=null;
                    mRotationId=null;
                    invalidate();
                }
                i(TAG, "onTouchEvent: ACTION_UP");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                i(TAG, "onTouchEvent: ACTION_POINTER_UP");
                if (mCurrentBox != null) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        curr = new PointF(event.getX(i), event.getY(i));
                        if (event.getPointerId(i) == mMainId) {
                            mCurrentBox.setCurrent(curr);

                        } else if (mRotationId!=null && event.getPointerId(i) == mRotationId) {
                            mCurrentBox.setAngle(Geometry.angleBetween(
                                    mCurrentBox.getCurrent(), mRotationPoint, curr)
                                    + mCurrentBox.getAngle());
                            mRotationPoint = curr;

                        }
                    }
                    invalidate();

                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentBox = null;
                mMainId=null;
                mRotationId=null;
                invalidate();
                i(TAG, "onTouchEvent: ACTION_CANCEL");
                break;


        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        for (Box lBox : mBoxes) {

            canvas.drawPath(lBox.getAsPath(), mBoxPaint);
        }
        if (mCurrentBox != null) {

            canvas.drawPath(mCurrentBox.getAsPath(), mBoxPaint);
        }



    }
}
