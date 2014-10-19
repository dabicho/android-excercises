package mx.org.dabicho.photogallery;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by dabicho on 18/10/14.
 */
public class BitmapCacheManager {

    static private BitmapCacheManager cacheManager;

    private LruCache<String,Bitmap> lruCache;

    private BitmapCacheManager() {
        lruCache=new LruCache<String,Bitmap>(250);
    }

    static public BitmapCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new BitmapCacheManager();
        }
        return cacheManager;
    }

    public void put(String key, Bitmap value){
        lruCache.put(key,value);
    }

    public Bitmap get(String key){
        return lruCache.get(key);
    }
}
