package mx.org.dabicho.photogallery;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Hilo que descarga las imágenes bajo demanda
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    LruCache<String,Bitmap> mCache;

    Handler mHandler;
    Handler mResponseHandler;

    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

    Listener<Token> mListener;
    public interface Listener<Token>{
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener=listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler=responseHandler;
        mCache=new LruCache<String,Bitmap>(150);
    }

    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==MESSAGE_DOWNLOAD){

                    Token token=(Token)msg.obj;
                    Log.i(TAG,"Got a request for url: "+requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
        super.onLooperPrepared();
    }

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null)
                return;
            final byte[] bitmapBytes;
            final Bitmap lBitmap;
            if(mCache.get(url)==null) {
                bitmapBytes = new FlickrFetcher().getUrlBytes(url);


                lBitmap= BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mCache.put(url,lBitmap);
                Log.i(TAG, "Bitmap created");
            } else {
                lBitmap=mCache.get(url);
            }

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token)!=url){
                        return;
                    } requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, lBitmap);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();

    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
