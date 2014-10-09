package mx.org.dabicho.criminal.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by dabicho on 10/8/14.
 */
public class PictureUtils {

    private static final String TAG="PictureUtils";

    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Display lDisplay=a.getWindowManager().getDefaultDisplay();
        float destWidth=lDisplay.getWidth();
        float destHeight=lDisplay.getHeight();

        BitmapFactory.Options lOptions=new BitmapFactory.Options();

        lOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,lOptions);
        float srcWidth=lOptions.outWidth;
        float srcHeight=lOptions.outHeight;

        int inSampleSize=1;

        if(srcHeight>destHeight||srcWidth>destWidth) {
            if(srcWidth>srcHeight) {
                inSampleSize=Math.round(srcHeight/destHeight);
            } else {
                inSampleSize=Math.round(srcWidth/destWidth);
            }
        }
        lOptions=new BitmapFactory.Options();
        lOptions.inSampleSize=inSampleSize;
        Bitmap lBitmap=BitmapFactory.decodeFile(path,lOptions);

        try {
            ExifInterface exif = new ExifInterface(path);
            Log.d(TAG,"ORIENTACION: "+ exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,-1));
        } catch (IOException e){
            Log.e(TAG, "exif error",e);
        }

        return new BitmapDrawable(a.getResources(), lBitmap);
    }

    public static void cleanImageView(ImageView imageView){
        if(!(imageView.getDrawable() instanceof  BitmapDrawable)){
            return;

        }

        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
