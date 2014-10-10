package mx.org.dabicho.criminal.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.UUID;

import mx.org.dabicho.criminal.api.CriminalIntentJSONSerializer;

/**
 * Created by dabicho on 10/1/14.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "CrimeLab.json";

    private static CriminalIntentJSONSerializer mSerializer;
    private ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private boolean saved;

    private CrimeLab(Context appContext) {
        mAppContext = appContext;

        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

        try{
            mCrimes=mSerializer.loadCrimes();
            saved=true;

        } catch(IOException|JSONException e) {
            mCrimes=new ArrayList<Crime>();
            Log.e(TAG, "CrimeLab Error cargando los crímenes: ",e);
        }

    }

    /**
     * @param c
     * @return La instancia del laboratorio de crímenes
     */
    public static CrimeLab getInstance(Context c) {
        if (sCrimeLab == null) {

            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    /**
     * Obtiene el crimen dado su id
     *
     * @param id id del crimen buscado
     * @return un crimen con el id
     */
    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
        saved=false;
    }

    public boolean saveCrimes(){
        try {
            mSerializer.saveCrimes(mCrimes);

            saved=true;
            return true;
        } catch(JSONException|IOException e){
            Log.e(TAG, "saveCrimes Error saving crimes: ",e);
            return false;
        }

    }

    /**
     * Elimina el crimen si existe en el laboratorio
     * @param c
     */
    public void deleteCrime(Crime c){
        mCrimes.remove(c);
        deleteCrimePhoto(c);
        // No se pone saved en false, por que ya lo hizo deleteCrimePhoto
    }

    /**
     * Removes the photo from a crime and deletes its file
     * @param c
     */
    public void deleteCrimePhoto(Crime c){
        if(c.getPhoto()!=null) {
            File photoFile = mAppContext.getFileStreamPath(c.getPhoto().getFilename());
            if (photoFile.exists())
                if (photoFile.delete())
                    c.setPhoto(null);
                else
                    Log.e(TAG, "deleteCrimePhoto(): Crime Photo could not be deleted");
        }
        saved=false;

    }
}
