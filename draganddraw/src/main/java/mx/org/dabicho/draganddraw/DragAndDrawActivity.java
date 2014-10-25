package mx.org.dabicho.draganddraw;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;


public class DragAndDrawActivity extends SingleSupportFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DragAndDrawFragment();
    }


}
