package mx.org.dabicho.criminal.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Utils for working with images and pictures
 */
public class PictureUtils {

    private static final String TAG = "PictureUtils";

    /**
     * Gets a scaled drawable to fit the displa from an image file with the transformation required by exif
     * @param a the activity which holds the display where it will be presented
     * @param path path to the image file
     * @return transformed bitmap
     */
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Display lDisplay = a.getWindowManager().getDefaultDisplay();
        float destWidth = lDisplay.getWidth();
        float destHeight = lDisplay.getHeight();

        BitmapFactory.Options lOptions = new BitmapFactory.Options();

        lOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, lOptions);
        float srcWidth = lOptions.outWidth;
        float srcHeight = lOptions.outHeight;

        int inSampleSize = 1;

        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        lOptions = new BitmapFactory.Options();
        lOptions.inSampleSize = inSampleSize;
        Bitmap lBitmap = BitmapFactory.decodeFile(path, lOptions);
        Matrix lMatrix = new Matrix();

        try {
            ExifInterface exif = new ExifInterface(path);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    lMatrix.setRotate(90);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    lMatrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    lMatrix.setRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    lMatrix.setScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    lMatrix.setScale(1,-1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    lMatrix.setScale(-1,1);
                    lMatrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    lMatrix.setRotate(90);
                    lMatrix.postScale(-1,1);
                    break;
                default:

            }
            Log.d(TAG, "ORIENTATION: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
        } catch (IOException e) {
            Log.e(TAG, "exif error", e);
        }


        Bitmap resultBitmap=Bitmap.createBitmap(lBitmap,0,0,lBitmap.getWidth(),lBitmap.getHeight(),lMatrix,true);
        lBitmap.recycle();

        return new BitmapDrawable(a.getResources(), resultBitmap);
    }

    /**
     * Cleans an imageView and recycles its drawable
     * @param imageView
     */
    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;

        }

        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
