package mx.org.dabicho.criminal;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import mx.org.dabicho.criminal.model.Crime;
import mx.org.dabicho.criminal.model.CrimeLab;

/**
 * Fragmento que maneja la lista de crímenes
 */
public class CrimeListFragment extends ListFragment {
    private final static String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;

    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        mSubtitleVisible = false;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v;
        //v = super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getActivity().getActionBar() != null) {
            if (mSubtitleVisible)
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
        }

        Button creaActividadButton = (Button) v.findViewById(R.id.add_crimeButton);
        creaActividadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creaCrimen();
            }
        });
        // EL método getListView regresa null hasta que se ejecuta onActivityCreated
        // Por lo quepara obtener el ListView, se busca por id, con id list
        ListView lListView = (ListView) v.findViewById(android.R.id.list);
        registerForContextMenu(lListView);

        return v;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Muestra texto por default cuando la lista está vacía
        // Remplazar por layout
        //setEmptyText(getString(R.string.no_crimes));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);

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
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            Crime c = getItem(position);

            TextView lTitleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            lTitleTextView.setText(c.getTitle());
            TextView lDateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            lDateTextView.setText(DateFormat.format("EEEE, MMM d, yyyy ", c.getDate()) + "a las" + DateFormat.format(" HH:mm:ss", c.getDate()));
            CheckBox lSolvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            lSolvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);

        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);

    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                creaCrimen();

                return true;
            case R.id.menu_item_show_subtitle:
                if (getActivity().getActionBar().getSubtitle() == null) {
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    item.setTitle(R.string.hide_subtitle);
                    mSubtitleVisible = true;
                } else {
                    getActivity().getActionBar().setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {

            case R.id.menu_item_delete_crime:

                CrimeLab.getInstance(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Crea y agrega un crimen al laboratorio de crímenes
     */
    private void creaCrimen() {
        Crime c = new Crime();
        CrimeLab.getInstance(getActivity()).addCrime(c);
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivityForResult(i, 0);
    }
}
