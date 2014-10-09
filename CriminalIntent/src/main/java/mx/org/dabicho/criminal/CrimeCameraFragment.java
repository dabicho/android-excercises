package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import mx.org.dabicho.criminal.api.Globals;

/**
 * Fragmento para dar soporte a la cámara
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "mx.org.dabicho.criminal.photo_filename";

    private Camera mCamera;
    private View mProgressContainer;

    private SurfaceView mSurfaceView;

    private OrientationEventListener mOrientationEventListener;


    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String filename = UUID.randomUUID().toString() + ".jpg";

            FileOutputStream os = null;

            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
                if (success) {
                    Log.i(TAG, "JPEG saved at " + filename);
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                    getActivity().setResult(Activity.RESULT_OK, i);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
                getActivity().finish();
            }
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        // El progress container intercepta eventos del dedo por lo que es necesario hacerlo
        // invisible primero
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    Log.d(TAG, "RotacionDisplay: " + getActivity().getWindowManager().getDefaultDisplay().getRotation());
                    Log.d(TAG, "Orientación: " + getActivity().getResources().getConfiguration().orientation);
                    Camera.CameraInfo lCameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, lCameraInfo);
                    Log.d(TAG, "OrientacionCameraInfo Front: " + lCameraInfo.orientation);
                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, lCameraInfo);
                    Log.d(TAG, "OrientacionCameraInfo Back: " + lCameraInfo.orientation);
                    int rotacion = 0;
                    switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
                        case Surface.ROTATION_0:
                            rotacion = 0;
                            break;
                        case Surface.ROTATION_180:
                            rotacion = 180;
                            break;
                        case Surface.ROTATION_270:
                            rotacion = 270;
                            break;
                        case Surface.ROTATION_90:
                            rotacion = 90;
                            break;
                        default:
                    }
                    Log.d(TAG, "Rotacion: " + rotacion);
                    Camera.Parameters lParameters = mCamera.getParameters();
                    lParameters.setRotation(rotacion);
                    mCamera.setParameters(lParameters);
                    mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
                }
            }
        });
        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        // Pre HoneyComb
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);

                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null)
                    return;
                mCamera.stopPreview();
                //TODO put this somewhere else where it is loaded at the start of the application
                if (Globals.getNaturalOrientation() == null) {
                    Globals.setNaturalOrientation(getDeviceDefaultOrientation());
                }

                int lRotation=0;
                switch (Globals.getNaturalOrientation()) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:

                        Camera.CameraInfo ci=new Camera.CameraInfo();
                        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, ci);
                        lRotation=ci.orientation;
                        Log.d(TAG,"Orientacion Portrait: Rotacion"+lRotation);
                        break;
                }

                Camera.Parameters lParameters = mCamera.getParameters();
                Camera.Size s;
                if(getActivity().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
                    s = getBestSupportedSize(lParameters.getSupportedPreviewSizes(), width, height);
                else
                    s = getBestSupportedSize(lParameters.getSupportedPreviewSizes(), height, width);
                lParameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(lParameters);
                if(getActivity().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
                    s = getBestSupportedSize(lParameters.getSupportedPictureSizes(), width, height);
                else
                    s = getBestSupportedSize(lParameters.getSupportedPictureSizes(), height, width);
                lParameters.setPictureSize(s.width, s.height);
                Log.d(TAG, "Selected Size: " + lParameters.getPreviewSize().width + " " + lParameters.getPreviewSize().height);
                mCamera.setParameters(lParameters);


                switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_0:


                        break;
                    case Surface.ROTATION_90:
                        // this mean the device was rotated 90° counter-clock wise,
                        // so it follows that rotation is 90 - rotation
                        lRotation = 90-lRotation;
                        break;
                    case Surface.ROTATION_180:

                        lRotation = 180-lRotation;
                        break;
                    case Surface.ROTATION_270:
                        lRotation=270-lRotation;
                        break;
                }

                mCamera.setDisplayOrientation(lRotation);



                mCamera.setParameters(lParameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "surfaceChanged could not start preview for some reason", e);
                    mCamera.release();
                    mCamera = null;
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null)
                    mCamera.stopPreview();
            }
        });
        return v;

    }



    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)height / (double)width;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        double oldRatio=0;
        int targetHeight = height;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * @return El tipo de orientación natural del dispositivo
     */
    private int getDeviceDefaultOrientation() {

        WindowManager windowManager = getActivity().getWindowManager();

        Configuration config = getActivity().getResources().getConfiguration();

        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

}
