package mx.org.dabicho.criminal;

import android.annotation.TargetApi;

import android.app.Activity;

import android.content.Intent;
import android.database.DataSetObserver;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
 * La actividad que hospede a este fragmento debe implementar la interfaz @link{Callbacks}
 */
public class CrimeListFragment extends ListFragment {
    private final static String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;

    private Callbacks mCallbacks;

    private ArrayList<Crime> selectedCrimes=new ArrayList<>();

    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        mSubtitleVisible = false;


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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


        // Para permitir selecciń múltiple. que está disponible en HoneyComb y posteriores
        // únicamente
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(lListView);
        } else {
            lListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            lListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {



                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    CrimeAdapter adapter=(CrimeAdapter)getListAdapter();

                    Crime c = adapter.getItem(position);
                    if(checked){

                        selectedCrimes.add(c);
                    }else {

                        selectedCrimes.remove(c);
                    }

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter lAdapter=(CrimeAdapter)getListAdapter();
                            CrimeLab lCrimeLab=CrimeLab.getInstance(getActivity());
                            for (int i= lAdapter.getCount()-1; i>=0; i--) {
                                if(getListView().isItemChecked(i))
                                    lCrimeLab.deleteCrime(lAdapter.getItem(i));
                            }
                            mode.finish();
                            lAdapter.notifyDataSetChanged();

                            return true;
                        case R.id.menu_item_delete_crime_photo:
                            for(Crime c:selectedCrimes) {
                                CrimeLab.getInstance(getActivity()).deleteCrimePhoto(c);
                            }

                            return true;

                        case R.id.menu_item_solve_crime:
                            for(Crime c:selectedCrimes) {
                                c.setSolved(true);
                            }

                            lAdapter=(CrimeAdapter)getListAdapter();
                            lAdapter.notifyDataSetChanged();
                            return true;

                        case R.id.menu_item_unsolve_crime:
                            for(Crime c:selectedCrimes) {
                                c.setSolved(false);
                            }

                            lAdapter=(CrimeAdapter)getListAdapter();
                            lAdapter.notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    selectedCrimes.clear();
                }
            });
        }


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
        mCallbacks.onCrimeSelected(c);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks=(Callbacks)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    /**
     * ArrayAdapter para uso de la acitividad de crímenes utilizando CrimeLab como fuente de datos
     */
    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
            registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();

                    CrimeLab.getInstance(getActivity()).saveCrimes();
                }
            });
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
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
        mCallbacks.onCrimeSelected(c);
    }

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    public void updateUI() {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }
}
