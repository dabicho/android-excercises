package mx.org.dabicho.criminal;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

/**
 Fragmento para dar soporte a la cámara
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG="CrimeCameraFragment";

    private Camera mCamera;

    private SurfaceView mSurfaceView;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_crime_camera, container, false);

        Button takePictureButton=(Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mSurfaceView=(SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder=mSurfaceView.getHolder();
        // Pre HoneyComb
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if(mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e){
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(mCamera==null)
                    return;

                Camera.Parameters lParameters=mCamera.getParameters();
                Camera.Size s=getBestSupportedSize(lParameters.getSupportedPreviewSizes(),width,height);
                mCamera.setParameters(lParameters);

                Log.d(TAG,"Parametros: "+lParameters.getPreviewSize().width+" "+lParameters.getPreviewSize().height);
                mCamera.setParameters(lParameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "surfaceChanged could not start preview for some reason",e);
                    mCamera.release();
                    mCamera=null;
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mCamera!=null)
                    mCamera.stopPreview();
            }
        });
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
            mCamera=Camera.open(0);
        } else {
            mCamera=Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mCamera!=null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize=sizes.get(0);
        Log.d(TAG,"TargetSize: "+width+"x"+height);
        int largestArea=bestSize.width*bestSize.height;
        for(Camera.Size s:sizes) {
            //if(s.width>width||s.height>height)
            //    continue;
            int area=s.width*s.height;
            Log.d(TAG,"Tamaño: "+s.width+"x"+s.height);
            if(area>largestArea) {
                bestSize=s;
                largestArea=area;
            }
        }
        return bestSize;
    }
}
