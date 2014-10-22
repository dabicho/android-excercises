package mx.org.dabicho.photogallery;

import android.net.Uri;
import android.util.Log;
import android.widget.Gallery;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mx.org.dabicho.photogallery.model.GalleryItem;

/**
 * Clase encargada de interactuar con flickr
 */
public class FlickrFetcher {
    private static final String TAG = "FlickrFetcher";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "54638c100889202547f72eb101c924ba";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_TEXT = "text";
    private static final String PARAM_SORT = "sort";

    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID="lastResultId";

    private static Integer currentPage = 1;
    /**
     * El nombre del elemento en el XML devuelto por flickr
     */
    private static final String XML_PHOTO = "photo";
    /**
     * El nombre del elemeto en el XML con el total de fotos
     */
    private static final String XML_PHOTOS = "photos";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL lURL = new URL(urlSpec);
        HttpURLConnection lConnection = (HttpURLConnection) lURL.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = lConnection.getInputStream();
            if (lConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            lConnection.disconnect();
        }
    }

    /**
     * Obtiene el documento urlSpec como una cadena
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * Obtiene la lista de GalleryItem que descarga del url
     *
     * @param url
     * @return
     */
    public FlickrResult downloadGalleryItems(String url) {
        FlickrResult lFlickrResult=new FlickrResult();
        currentPage++;
        return downloadGalleryItems(url, currentPage);
    }

    public FlickrResult downloadGalleryItems(String url, int currentPage) {
        FlickrResult lFlickrResult=new FlickrResult();

        try {


            String xmlString = getUrl(url);

            XmlPullParserFactory lFactory = XmlPullParserFactory.newInstance();
            XmlPullParser lParser = lFactory.newPullParser();
            lParser.setInput(new StringReader(xmlString));
            parseItems(lFlickrResult, lParser);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items for " + url, ioe);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return lFlickrResult;
    }

    /**
     * Obtiene los GalleryItem recientes de la página actual
     *
     * @return
     */
    public ArrayList<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_PAGE, currentPage.toString())
                .appendQueryParameter(PARAM_SORT,"date-posted-desc")
                .build().toString();
        return downloadGalleryItems(url).getItems();
    }

    public ArrayList<GalleryItem> fetchItems(int page) {
        String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_PAGE, currentPage.toString())
                .appendQueryParameter(PARAM_SORT,"date-posted-desc")
                .build().toString();
        return downloadGalleryItems(url,page).getItems();
    }

    /**
     * Obtiene los gallery items de la búsqueda de la página actual
     *
     * @param query
     * @return
     */
    public FlickrResult search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_PAGE, currentPage.toString())
                .appendQueryParameter(PARAM_TEXT, query)
                .appendQueryParameter(PARAM_SORT,"date-posted-desc")
                .build().toString();
        return downloadGalleryItems(url);
    }

    public FlickrResult search(String query, int page) {
        String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_PAGE, currentPage.toString())
                .appendQueryParameter(PARAM_TEXT, query)
                .appendQueryParameter(PARAM_SORT,"date-posted-desc")
                .build().toString();
        return downloadGalleryItems(url,page);
    }

    void parseItems(FlickrResult flickrResult, XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG &&
                    XML_PHOTOS.equals(parser.getName()))
            {
                flickrResult.setItemsFound(Long.parseLong(parser.getAttributeValue(null,"total")));
            }else if (eventType == XmlPullParser.START_TAG &&
                    XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                flickrResult.add(item);
            }
            eventType = parser.next();
        }

    }

    public static void resetPageCount() {
        currentPage = 0;
    }
}

class FlickrResult {
    private long mItemsFound=0;
    private ArrayList<GalleryItem> mItems;

    public FlickrResult(){
        mItemsFound=0;
        mItems=new ArrayList<GalleryItem>();
    }

    void setItemsFound(long found) {
        mItemsFound=found;
    }


    public long getItemsFound() {
        return mItemsFound;
    }

    public ArrayList<GalleryItem> getItems() {
        return mItems;
    }

    public void add(GalleryItem item) {
        mItems.add(item);
    }
}
