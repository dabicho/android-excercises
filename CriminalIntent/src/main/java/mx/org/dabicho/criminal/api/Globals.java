package mx.org.dabicho.criminal.api;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;

/**
 * Created by dabicho on 10/8/14.
 */
public class Globals {
    private static Integer NaturalOrientation=null;

    public static Integer getNaturalOrientation() {
        return NaturalOrientation;
    }

    public static void setNaturalOrientation(Integer naturalOrientation) {
        NaturalOrientation = naturalOrientation;
    }

    public static boolean canSendText(PackageManager pm) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        return pm.queryIntentActivities(i,0).size()>0;
    }

    public static boolean canPickConcact(PackageManager pm){
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        return pm.queryIntentActivities(i,0).size()>0;
    }
}
