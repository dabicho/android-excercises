package mx.org.dabicho.criminal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
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


import java.lang.annotation.Target;
import java.util.Date;
import java.util.UUID;

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
    private static final String DIALOG_IMAGE="image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO=2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoThumbnailView;

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
                Log.d(TAG, "onClick datePicker");
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
                Log.d(TAG, "onClick timePicker");
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
            }
        });

        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CrimeCameraActivity.class);

                startActivityForResult(i,REQUEST_PHOTO);

            }
        });

        mPhotoThumbnailView=(ImageView)v.findViewById(R.id.crime_imageView);

        PackageManager pm=getActivity().getPackageManager();
        boolean hasACamera=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras()>0);
        if(!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo lPhoto=mCrime.getPhoto();
                if(lPhoto==null)
                    return;
                FragmentManager fm=getActivity().getSupportFragmentManager();

                String path=getActivity().getFileStreamPath(lPhoto.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoThumbnailView);
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
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date lDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(lDate);
            mDateButton.setText(updateDate());
            mTimeButton.setText(updateTime());
        } else if (requestCode == REQUEST_TIME) {
            Date lDate = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(lDate);
            mDateButton.setText(updateDate());
            mTimeButton.setText(updateTime());
        } else if (requestCode==REQUEST_PHOTO) {
            String filename=data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if(filename!=null){
                Photo lPhoto=new Photo(filename);
                mCrime.setPhoto(lPhoto);
                Log.i(TAG, "Crime: "+mCrime.getTitle()+" has a photo");
                showPhoto();
            }
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
        super.onPause();
        CrimeLab.getInstance(getActivity()).saveCrimes();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

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

    private void showPhoto(){
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if(p!=null){
            String path=getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b= PictureUtils.getScaledDrawable(getActivity(),path);
        }
        mPhotoThumbnailView.setImageDrawable(b);
    }

}
