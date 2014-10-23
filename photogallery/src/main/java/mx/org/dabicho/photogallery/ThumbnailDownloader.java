package mx.org.dabicho.photogallery;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mx.org.dabicho.photogallery.services.BitmapCacheManager;

/**
 * Hilo que descarga las imágenes bajo demanda
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD_CACHE=1;

    public static final String PRELOAD_CACHE_URL="org.mx.dabicho.thumbnailUrl";


    Handler mHandler;
    Handler mResponseHandler;

    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

    Listener<Token> mListener;

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;

    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {

                    Token token = (Token) msg.obj;
                    Log.d(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                } else if(msg.what == MESSAGE_PRELOAD_CACHE) {
                    String url =(String) msg.obj;
                    if(url!=null && BitmapCacheManager.getInstance().get(url)==null) {
                        Log.d(TAG, "Got a request to pre-cache: ");
                        handleCacheRequest(url);
                    } else
                        Log.d(TAG, "Got a request to pre-cache: already present");
                }
            }
        };
        super.onLooperPrepared();
    }

    /**
     * Maneja una petición para agregar un url al cache. Menor prioridad que handleRequest
     * @param url
     */
    private void handleCacheRequest(String url){

            try {
                final byte[] bitmapBytes;
                final Bitmap lBitmap;

                bitmapBytes = new FlickrFetcher().getUrlBytes(url,true);
                lBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);


                BitmapCacheManager.getInstance().put(url, lBitmap);
            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading image", ioe);
            }

    }


    /**
     * Maneja una petición para descargar una imagen. El token se utiliza para actualizar datos con
     * la interfaz y referenciar el token con el url en un mapa
     * @param token
     */
    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null)
                return;
            final byte[] bitmapBytes;
            final Bitmap lBitmap;

            bitmapBytes = new FlickrFetcher().getUrlBytes(url,true);


            lBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);


            BitmapCacheManager.getInstance().put(url, lBitmap);

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url) {
                        return;
                    }
                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, lBitmap);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    /**
     * Encolar una solicitud para agregar un url al cache
     * @param url
     */
    public void queuePreloadCache(String url) {
        //Log.i(TAG,"Got a request for pre-cache: ");
        if (!mHandler.hasMessages(MESSAGE_PRELOAD_CACHE, url))
            mHandler.obtainMessage(MESSAGE_PRELOAD_CACHE, url).sendToTarget();
    }

    /**
     * Encolar una solicitud para procesar un url y un token
     * @param token
     * @param url
     */
    public void queueThumbnail(Token token, String url) {
        //Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);
        if (!mHandler.hasMessages(MESSAGE_DOWNLOAD, token)) {
            Message message=mHandler.obtainMessage(MESSAGE_DOWNLOAD, token);
            mHandler.sendMessageAtFrontOfQueue(message);
        }

    }

    public void dequeueThumbnail(Token token) {
        requestMap.remove(token);
    }

    /**
     * Limpia la cola de mensajes y el mapeo de peticiones
     */
    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
