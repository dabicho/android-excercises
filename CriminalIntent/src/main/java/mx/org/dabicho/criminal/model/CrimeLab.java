package mx.org.dabicho.criminal.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import java.util.UUID;

/**
 * Created by dabicho on 10/1/14.
 */
public class CrimeLab {
    private static final String TAG="CrimeLab";
    private ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext){
        mAppContext=appContext;
        mCrimes=new ArrayList<Crime>();

        //TODO Remover este bloque de código cuando ya no sea necesario tener datos de muestra
        for(int i=0; i<100; i++){
            Crime c=new Crime();
            c.setSolved(i%2==0);
            c.setTitle("Crime #"+i);
            mCrimes.add(c);
        }
    }

    /**
     *
     * @param c
     * @return La instancia del laboratorio de crímenes
     */
    public static CrimeLab getInstance(Context c){
        if(sCrimeLab==null) {
            Log.d(TAG,"getInstance creando instancia");
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes(){
        return mCrimes;
    }

    /**
     * Obtiene el crimen dado su id
     * @param id id del crimen buscado
     * @return un crimen con el id
     */
    public Crime getCrime(UUID id) {
        for(Crime c: mCrimes){
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }
}
