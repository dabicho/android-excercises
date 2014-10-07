package mx.org.dabicho.criminal.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

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

    private CrimeLab(Context appContext) {
        mAppContext = appContext;

        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

        try{
            mCrimes=mSerializer.loadCrimes();

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
            Log.d(TAG, "getInstance creando instancia");
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
    }

    public boolean saveCrimes(){
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "saveCrimes crimes saved to file");
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
    }
}
