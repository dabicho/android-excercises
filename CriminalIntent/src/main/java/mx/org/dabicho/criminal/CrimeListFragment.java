package mx.org.dabicho.criminal;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import mx.org.dabicho.criminal.model.Crime;
import mx.org.dabicho.criminal.model.CrimeLab;

/**
 * Fragmento que maneja la lista de crímenes
 */
public class CrimeListFragment extends ListFragment {
    private final static String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        CrimeAdapter adapter=new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c =  ((CrimeAdapter)getListAdapter()).getItem(position);

        // Originalmente se mostraba CriminalIntentActivity
        //Intent i=new Intent(getActivity(),CriminalIntentActivity.class);
        // Se regresa un intent para cargar CrimePagerActivity
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
                convertView=getActivity().getLayoutInflater().inflate(R.layout.list_item_crime,null);
            Crime c = getItem(position);

            TextView lTitleTextView=(TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            lTitleTextView.setText(c.getTitle());
            TextView lDateTextView=(TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            lDateTextView.setText(DateFormat.format("EEEE, MMM d, yyyy ",c.getDate())+"a las"+DateFormat.format(" HH:mm:ss",c.getDate()));
            CheckBox lSolvedCheckBox=(CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            lSolvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}
