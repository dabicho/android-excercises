package mx.org.dabicho.runtracker;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import mx.org.dabicho.runtracker.model.Run;


/**
 * Fragmento para presentar una lista con las corridas
 */
public class RunListFragment extends ListFragment {
    private static final String TAG = "RunListFragment";
    private static final int REQUEST_NEW_RUN = 0;
    private RunDatabaseHelper.RunCursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCursor = RunManager.getInstance(getActivity()).queryRuns();

        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mCursor);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunTrackerActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_NEW_RUN==requestCode) {
            mCursor.requery();
            ((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
        }


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Como la columna de índice se llama _id, el adapter de cursor detecta esto automáticamente
        // y la utiliza como el parámetro id de este método
        Intent i = new Intent(getActivity(), RunTrackerActivity.class);
        i.putExtra(RunTrackerActivity.EXTRA_RUN_ID, id);
        startActivity(i);
        super.onListItemClick(l, v, position, id);
    }

    private static class RunCursorAdapter extends CursorAdapter {
        private RunDatabaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();
            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            if(RunManager.getInstance(context).isTrackingRun(run)){
                startDateTextView.setBackgroundColor(Color.CYAN);
                startDateTextView.setTextAppearance(context,R.style.Tracking);
            } else {
                startDateTextView.setBackgroundColor(Color.BLACK);
                startDateTextView.setTextAppearance(context, R.style.NotTracking);
            }
            startDateTextView.setText(cellText);
        }
    }
}
