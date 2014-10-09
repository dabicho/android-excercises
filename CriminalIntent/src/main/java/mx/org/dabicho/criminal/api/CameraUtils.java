package mx.org.dabicho.criminal.api;

import android.content.Context;
import android.hardware.Camera;
import android.view.OrientationEventListener;
import android.view.Surface;

/**
 * Utils for the use of the camera
 */
public class CameraUtils {

    private static int mRotation=0;
    private static int mCameraId=0;

    private static OrientationEventListener mOrientationEventListener;

    /**
     * Sets up the orientation event listener if it does not exist.
     * The state is set to disabled if it was already loaded.
     *
     * The orientationEventListener sets up rotation according to the current cameraId
     * @param c Context
     */
    public static void loadOrientationEventListener(Context c){
        if(mOrientationEventListener!=null) {
            mOrientationEventListener.disable();
            return;
        }
        mOrientationEventListener=new OrientationEventListener(c) {
            @Override
            public void onOrientationChanged(int orientation) {
                // La rotación está indicada en sentido horario a partir de la orientación natural
                // Del dispositivo
                // Estamos suponiendo la cámara de atrás

                if(orientation==ORIENTATION_UNKNOWN) return;

                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(mCameraId, info);

                orientation=(orientation+45)/90*90;

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mRotation = (info.orientation - orientation + 360) % 360;
                } else {  // back-facing camera
                    mRotation = (info.orientation + orientation) % 360;
                }


            }

        };
    }

    /**
     *
     * @return the id of the first BackFacing camera
     */
    public static int getFirstBackFacingCameraID(){
        int lCamId=0;

        for(lCamId=0; lCamId<Camera.getNumberOfCameras(); lCamId++) {
            Camera.CameraInfo lCameraInfo=new Camera.CameraInfo();
            Camera.getCameraInfo(lCamId,lCameraInfo);
            if(lCameraInfo.facing== Camera.CameraInfo.CAMERA_FACING_BACK)
                return lCamId;

        }
        return 0;
    }

    /**
     *
     * @return the clockwise rotation needed to get the image correctly orientated
     */
    public static int getRotation() {
        return mRotation;
    }


    /**
     *
     * @return the current cameraId
     */
    public static int getCameraId() {
        return mCameraId;
    }

    /**
     *
     * @param CameraId the cameraId
     */
    public static void setCameraId(int CameraId) {
        CameraUtils.mCameraId = CameraId;
    }

    /**
     *
     * @return the orientationEventListener
     */
    public static OrientationEventListener getOrientationEventListener() {
        return mOrientationEventListener;
    }

    /**
     * Disables the orientationEventListener
     */
    public static void disableOrientationEventListener(){
        if(mOrientationEventListener!=null)
            mOrientationEventListener.disable();
    }

    /**
     * Enables the orientationEventListener
     */
    public static void enableOrientationEventListener(){
        if(mOrientationEventListener!=null){
            mOrientationEventListener.enable();
        }
    }
}
