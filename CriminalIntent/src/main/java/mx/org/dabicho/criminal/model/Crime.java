package mx.org.dabicho.criminal.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Un crimen
 */
public class Crime {

    private static final String JSON_ID="id";
    private static final String JSON_TITLE="title";
    private static final String JSNO_SOLVED="solved";
    private static final String JSON_DATE="date";
    private static final String JSON_PHOTO="photo";
    private static final String JSON_SUSPECT="suspect";

    private Photo mPhoto;
    private String mSuspect;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;


    public Crime() {
        mId = UUID.randomUUID();
        mDate=new Date();

    }

    public Crime(JSONObject json) throws JSONException{
        mId = UUID.fromString(json.getString(JSON_ID));
        if(json.has(JSON_TITLE))
            mTitle=json.getString(JSON_TITLE);
        if(json.has(JSON_SUSPECT))
            mSuspect=json.getString(JSON_SUSPECT);
        mSolved=json.getBoolean(JSNO_SOLVED);
        mDate=new Date(json.getLong(JSON_DATE));
        if(json.has(JSON_PHOTO))
            mPhoto=new Photo(json.getJSONObject(JSON_PHOTO));
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Photo getPhoto(){
        return mPhoto;
    }

    public void setPhoto(Photo photo){
        mPhoto=photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json=new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE,mTitle);
        json.put(JSON_SUSPECT,mSuspect);
        json.put(JSNO_SOLVED,mSolved);
        json.put(JSON_DATE,mDate.getTime());
        if(mPhoto!=null)
            json.put(JSON_PHOTO,mPhoto.toJSON());
        return json;
    }

    @Override
    public String toString() {
        return mTitle;
    }


}
