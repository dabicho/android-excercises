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
import android.view.LayoutInflater;
import android.view.OrientationEventListener;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import mx.org.dabicho.criminal.api.CameraUtils;


/**
 * Fragmento para dar soporte a la cámara
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "mx.org.dabicho.criminal.photo_filename";

    private Camera mCamera;
    private View mProgressContainer;

    private SurfaceView mSurfaceView;




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
                    int rotacion = 0;
                    rotacion=CameraUtils.getRotation();
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

                Camera.Parameters lParameters = mCamera.getParameters();
                Camera.Size s=getBestSupportedSize(lParameters.getSupportedPreviewSizes(), width, height);
                lParameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(lParameters);
                s = getBestSupportedSize(lParameters.getSupportedPictureSizes(), width, height);
                lParameters.setPictureSize(s.width, s.height);
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


    /**
     * Opens a backFacing camera and enables the OrientationEventListener from CameraUtils to keep
     * track of the rotation
     */
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            int cameraId=CameraUtils.getFirstBackFacingCameraID();
            mCamera = Camera.open(cameraId);
            CameraUtils.setCameraId(cameraId);

            CameraUtils.enableOrientationEventListener();
        } else {
            mCamera = Camera.open();

        }
    }

    /**
     * Releases the camera and disables the orientationEventListener from CameraUtils
     * when the fragment is paused
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        CameraUtils.disableOrientationEventListener();
    }

    /**
     * Gets the best supported size that resembles the aspect ratio with a tolerance of 0.1
     * If none is found, it returns the size with the closest height to the target
     * @param sizes the sizes from wich one will be selected
     * @param width the target width to match
     * @param height the target height to match
     * @return the selected size
     */
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



}
