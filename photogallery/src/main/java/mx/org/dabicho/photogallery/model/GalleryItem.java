package mx.org.dabicho.photogallery.model;

/**
 * Elemento de una galería
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    /**
     *
     * @return el url de la página de la imagen
     */
    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+ mOwner +"/"+mId;
    }
}
