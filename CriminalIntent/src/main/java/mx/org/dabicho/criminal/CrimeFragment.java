package mx.org.dabicho.criminal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import mx.org.dabicho.criminal.api.Globals;
import mx.org.dabicho.criminal.api.PictureUtils;
import mx.org.dabicho.criminal.model.Crime;
import mx.org.dabicho.criminal.model.CrimeLab;
import mx.org.dabicho.criminal.model.Photo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrimeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/*
Utilizamos fragment de support v4 por que es dependencia requerida por CrimePagerActivity
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";


    public static final String EXTRA_CRIME_ID = "mx.org.dabicho.criminal.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_CONTACT = 3;
    private Callbacks mCallbacks;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoThumbnailView;
    private Button suspectButton;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param crimeId el ID del crimen para buscarlo en el laboratorio CrimeLab
     * @return A new instance of fragment CrimeFragment.
     */

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        fragment.setArguments(args);
        return fragment;
    }

    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        // Obtener el crimen seleccionado utilizando los extra del activity
        /*
        UUID id=(UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        */
        // Obtener el crimen seleccionado utilizando los argumentos del fragment
        // Puede ser buena idea revisar si es nulo
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);

    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        // Esta pieza de código es ejecutada únicamente si el SO anfitrión
        // implementa el API de HONEYCOMB o superior
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getActivity().getActionBar() != null) {

            if (NavUtils.getParentActivityName(getActivity()) != null)
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mCrime.setTitle(s.toString());
                mCallbacks.onCrimeUpdated(mCrime);
                getActivity().setTitle(mCrime.getTitle());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(updateDate());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                // Se indica el fragmento objetivo como este mismo, para que sepa a quien devolver
                // la respuesta
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mTimeButton.setText(updateTime());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);

                startActivityForResult(i, REQUEST_PHOTO);

            }
        });

        mPhotoThumbnailView = (ImageView) v.findViewById(R.id.crime_imageView);

        registerForContextMenu(mPhotoThumbnailView);


        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo lPhoto = mCrime.getPhoto();
                if (lPhoto == null)
                    return;
                FragmentManager fm = getActivity().getSupportFragmentManager();

                String path = getActivity().getFileStreamPath(lPhoto.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);

        if (!Globals.canSendText(getActivity().getPackageManager()))
            reportButton.setEnabled(false);
        else
            reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);

                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                    startActivity(i);
                }
            });

        suspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        if (!Globals.canPickConcact(getActivity().getPackageManager()))
            suspectButton.setEnabled(false);
        else
            suspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, REQUEST_CONTACT);

                }
            });
        if (mCrime.getSuspect() != null)
            suspectButton.setText(mCrime.getSuspect());
        Log.d(TAG,"return onCreateView");
        return v;


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart call showPhoto");
        showPhoto();
        Log.d(TAG,"onStart return showPhoto");
    }

    @Override
    public void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();
        PictureUtils.cleanImageView(mPhotoThumbnailView);
        Log.d(TAG,"return onStop");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"onAttach");
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mCallbacks = (Callbacks) activity;
        Log.d(TAG,"return onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        mListener = null;
        mCallbacks = null;
        Log.d(TAG,"return onDetach");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.crime_imageView) {
            getActivity().getMenuInflater().inflate(R.menu.fragment_crime_image_view, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime_photo:
                CrimeLab.getInstance(getActivity()).deleteCrimePhoto(mCrime);
                PictureUtils.cleanImageView(mPhotoThumbnailView);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date lDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(lDate);
            mDateButton.setText(updateDate());
            mTimeButton.setText(updateTime());
            mCallbacks.onCrimeUpdated(mCrime);
        } else if (requestCode == REQUEST_TIME) {
            Date lDate = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(lDate);
            mDateButton.setText(updateDate());
            mTimeButton.setText(updateTime());
            mCallbacks.onCrimeUpdated(mCrime);
        } else if (requestCode == REQUEST_PHOTO) {
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo lPhoto = new Photo(filename);
                CrimeLab.getInstance(getActivity()).deleteCrimePhoto(mCrime);
                mCrime.setPhoto(lPhoto);
                Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
                Log.d(TAG,"onActivityResult call showPhoto");
                showPhoto();
                Log.d(TAG,"onActivityResult return showPhoto");
            }
            Log.d(TAG,"onActivityResult update crime photo");
            mCallbacks.onCrimeUpdated(mCrime);
            Log.d(TAG,"onActivityResult update crime photo 2");
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI

            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (c.getCount() == 0) {
                c.close();
                return;
            }
            c.moveToFirst();
            long tid = c.getLong(1);
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            suspectButton.setText(suspect);
            String photoUri = c.getString(1);
            c.close();
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(Uri.parse(photoUri));
                Bitmap b = BitmapFactory.decodeStream(is);
                BitmapDrawable bd = new BitmapDrawable(getActivity().getResources(), b);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    suspectButton.setBackground(bd);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Imagen no encontrada");
            }
            mCallbacks.onCrimeUpdated(mCrime);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // los intent pueden tener banderas
                // Esta busca una actividad del mismo tipo y remueve todas lasposteriores para dejar
                // esta hasta arriba
                /*
                  Intent intent = new Intent(getActivity(), CrimeListActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);
                 */
                if (NavUtils.getParentActivityName(getActivity()) != null)
                    NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        CrimeLab.getInstance(getActivity()).saveCrimes();
        Log.d(TAG,"return onPause");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_image_view, menu);

        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    /**
     * @return Una cadena con la fecha formateada Día de la semana, Mes día, año
     */
    private String updateDate() {
        return DateFormat.format("EEEE, MMM dd, yyyy.", mCrime.getDate()).toString();
    }

    /**
     * @return Una cadena con la hora formateada HH:mm:ss
     */
    private String updateTime() {
        return DateFormat.format("HH:mm:ss", mCrime.getDate()).toString();
    }

    /**
     * Muestra la foto en el ImageView thel thubmnail
     */
    private static int showPhotocnt=0;
    private void showPhoto() {
        showPhotocnt++;
        Log.d(TAG,"showPhotoCount: "+showPhotocnt);
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }

        mPhotoThumbnailView.setImageDrawable(b);
        Log.d(TAG,"showPhoto setDrawable");
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EE, MMM dd, yyyy";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect != null)
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        else
            suspect = getString(R.string.crime_report_no_suspects);
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;

    }

    /**
     * Interfaz que debe implementar la actividad que contenga este fragmento
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

}
