package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dabicho on 10/2/14.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "mx.org.dabicho.criminalIntent.date";
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTime(mDate);
        int year = lCalendar.get(Calendar.YEAR);
        int month = lCalendar.get(Calendar.MONTH);
        int day = lCalendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        DatePicker lDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        lDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Se crea un calendario con la fecha seleccionada preservando la hora
                Calendar lCalendar1 = Calendar.getInstance();
                lCalendar1.setTime(mDate);
                mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth,
                        lCalendar1.get(Calendar.HOUR_OF_DAY),
                        lCalendar1.get(Calendar.MINUTE),0).getTime();

                //Se actualiza el argumento del fragment para que si por alguna razón
                // se crea el diálogo (p.ej. rotación), se utilice la nueva fecha
                // en lugar de usar onSaveInstanceState
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v).setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                }).create();
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();

        args.putSerializable(EXTRA_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        // onActivityResult es utilizado para indicar resultado de peticiones (intent)
        // resultCode indica el código de resultado
        // La petición se realizó indicando un fragmento objetivo (targetFragment)
        // y un código de identificación
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
